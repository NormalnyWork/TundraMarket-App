package com.normalnywork.tundramarket.sync

import androidx.room.withTransaction
import com.normalnywork.tundramarket.data.local.db.TMDatabase
import com.normalnywork.tundramarket.data.local.db.dao.OrdersDao
import com.normalnywork.tundramarket.data.local.db.dao.SyncOutboxDao
import com.normalnywork.tundramarket.data.local.db.entities.OrderNetworkStatusEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusHistoryEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOperationTypeEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOutboxEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOutboxStatusEntity
import com.normalnywork.tundramarket.data.local.db.mappers.toDomain
import com.normalnywork.tundramarket.data.local.preferences.OrderHistorySyncStore
import com.normalnywork.tundramarket.data.remote.source.RemoteOrdersDataSource
import com.normalnywork.tundramarket.domain.repositories.ConfigRepository
import com.normalnywork.tundramarket.domain.repositories.ProductRepository
import com.normalnywork.tundramarket.utils.NetworkStatusObserver
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Singleton
import java.io.IOException

@Singleton
class OutboxSyncProcessor(
    private val database: TMDatabase,
    private val ordersDao: OrdersDao,
    private val syncOutboxDao: SyncOutboxDao,
    private val remoteOrdersDataSource: RemoteOrdersDataSource,
    private val productRepository: ProductRepository,
    private val configRepository: ConfigRepository,
    private val orderHistorySyncStore: OrderHistorySyncStore,
    private val networkStatusObserver: NetworkStatusObserver,
) {

    suspend fun syncPendingOperations(
        retryWhenWaitingForNetwork: Boolean,
        syncCurrentOrderStatus: Boolean,
    ): SyncResult = mutex.withLock {
        resetStaleRunningOperations()

        if (!retryWhenWaitingForNetwork && !networkStatusObserver.isConnected()) {
            val hasWaitingOperations = markReadyOperationsWaitingForNetwork()
            val hasWaitingStatusUpdate = if (!syncCurrentOrderStatus || hasWaitingOperations) {
                false
            } else {
                markCurrentOrderStatusWaitingForNetwork()
            }
            return@withLock SyncResult(
                shouldRetry = retryWhenWaitingForNetwork && (hasWaitingOperations || hasWaitingStatusUpdate),
            )
        }

        var shouldRetry = false

        while (true) {
            val operation = claimNextOperation() ?: break
            val operationResult = syncOperation(
                operation = operation,
                retryWhenWaitingForNetwork = retryWhenWaitingForNetwork,
            )

            if (!operationResult.success) {
                shouldRetry = operationResult.retryWork
                break
            }
        }

        if (syncCurrentOrderStatus && !shouldRetry && !hasOutboxOperations()) {
            val statusUpdateResult = syncCurrentOrderStatus(retryWhenWaitingForNetwork)
            if (!statusUpdateResult.success) {
                shouldRetry = statusUpdateResult.retryWork
            }
        }

        SyncResult(shouldRetry = shouldRetry)
    }

    private suspend fun claimNextOperation(): SyncOutboxEntity? {
        val now = System.currentTimeMillis()

        return database.withTransaction {
            val operation = syncOutboxDao.getNextReadyOperation(
                now = now,
                statuses = READY_STATUSES,
            )

            if (operation != null) {
                syncOutboxDao.updateStatus(
                    operationId = operation.id,
                    status = SyncOutboxStatusEntity.Running,
                )
            }

            operation
        }
    }

    private suspend fun syncOperation(
        operation: SyncOutboxEntity,
        retryWhenWaitingForNetwork: Boolean,
    ): OperationResult {
        if (!retryWhenWaitingForNetwork && !networkStatusObserver.isConnected()) {
            markWaitingForNetwork(operation)
            return OperationResult(
                success = false,
                retryWork = retryWhenWaitingForNetwork,
            )
        }

        return when (operation.operationType) {
            SyncOperationTypeEntity.CreateOrder -> syncCreateOrder(operation)
            SyncOperationTypeEntity.CreateOrderForNomad -> syncCreateOrderForNomad(operation)
            SyncOperationTypeEntity.ChangeOrderStatus -> syncChangeOrderStatus(operation)
            SyncOperationTypeEntity.UpdateCatalog -> syncUpdateCatalog(operation)
            SyncOperationTypeEntity.UpdateTradingStations -> syncUpdateTradingStations(operation)
        }
    }

    private suspend fun syncCreateOrder(operation: SyncOutboxEntity): OperationResult {
        val order = ordersDao.getOrderByLocalId(operation.localEntityId)
            ?: return completeMissingLocalOrder(operation)
        if (order.order.serverId == null && order.order.status == OrderStatusEntity.Cancelled) {
            database.withTransaction {
                ordersDao.updateNetworkStatus(
                    localOrderId = operation.localEntityId,
                    networkStatus = null,
                )
                syncOutboxDao.delete(operation)
            }
            return OperationResult.Success
        }

        ordersDao.updateNetworkStatus(
            localOrderId = operation.localEntityId,
            networkStatus = OrderNetworkStatusEntity.Loading,
        )

        return runCatching {
            remoteOrdersDataSource.createOrder(
                order = order.toDomain(),
                idempotencyKey = operation.idempotencyKey,
            )
        }.fold(
            onSuccess = { serverOrderId ->
                database.withTransaction {
                    val existingOrder = ordersDao.getOrderByServerId(serverOrderId)
                    if (existingOrder != null && existingOrder.order.id != operation.localEntityId) {
                        deleteLocalOrder(operation.localEntityId)
                    } else {
                        ordersDao.markOrderSynced(
                            localOrderId = operation.localEntityId,
                            serverOrderId = serverOrderId,
                        )
                    }
                    syncOutboxDao.delete(operation)
                }
                OperationResult.Success
            },
            onFailure = { error ->
                markFailed(
                    operation = operation,
                    error = error,
                    retryWork = error.shouldRetryWork(),
                )
            },
        )
    }

    private suspend fun syncCreateOrderForNomad(operation: SyncOutboxEntity): OperationResult {
        val order = ordersDao.getOrderByLocalId(operation.localEntityId)
            ?: return completeMissingLocalOrder(operation)
        val nomadPhone = operation.comment
            ?: return markFailed(
                operation = operation,
                error = IllegalStateException("SMS order does not have nomad phone"),
                retryWork = false,
            )

        ordersDao.updateNetworkStatus(
            localOrderId = operation.localEntityId,
            networkStatus = OrderNetworkStatusEntity.Loading,
        )

        return runCatching {
            remoteOrdersDataSource.createOrderForNomad(
                nomadPhone = nomadPhone,
                location = order.order.location.toDomain(),
                products = ordersDao.getOrderProducts(operation.localEntityId).map { product ->
                    RemoteOrdersDataSource.ProductCount(
                        productId = product.productId,
                        count = product.count,
                    )
                },
                comment = order.order.comment.takeIf { it.isNotBlank() },
                idempotencyKey = operation.idempotencyKey,
            )
        }.fold(
            onSuccess = { serverOrderId ->
                database.withTransaction {
                    ordersDao.markOrderSynced(
                        localOrderId = operation.localEntityId,
                        serverOrderId = serverOrderId,
                    )
                    syncOutboxDao.delete(operation)
                }
                OperationResult.Success
            },
            onFailure = { error ->
                markFailed(
                    operation = operation,
                    error = error,
                    retryWork = error.shouldRetryWork(),
                )
            },
        )
    }

    private suspend fun syncChangeOrderStatus(operation: SyncOutboxEntity): OperationResult {
        val order = ordersDao.getOrderByLocalId(operation.localEntityId)
            ?: return completeMissingLocalOrder(operation)
        val serverOrderId = order.order.serverId
            ?: return markFailed(
                operation = operation,
                error = IllegalStateException("Order has not been created on server yet"),
                retryWork = false,
            )
        val status = order.toDomain().status

        ordersDao.updateNetworkStatus(
            localOrderId = operation.localEntityId,
            networkStatus = OrderNetworkStatusEntity.Updating,
        )

        return runCatching {
            remoteOrdersDataSource.changeOrderStatus(
                orderId = serverOrderId,
                status = status,
                idempotencyKey = operation.idempotencyKey,
                comment = operation.comment,
            )
        }.fold(
            onSuccess = {
                database.withTransaction {
                    ordersDao.updateNetworkStatus(
                        localOrderId = operation.localEntityId,
                        networkStatus = null,
                    )
                    syncOutboxDao.delete(operation)
                }
                OperationResult.Success
            },
            onFailure = { error ->
                markFailed(
                    operation = operation,
                    error = error,
                    retryWork = error.shouldRetryWork(),
                )
            },
        )
    }

    private suspend fun syncUpdateCatalog(operation: SyncOutboxEntity): OperationResult {
        return runCatching {
            productRepository.updateCatalog()
        }.fold(
            onSuccess = {
                syncOutboxDao.delete(operation)
                OperationResult.Success
            },
            onFailure = { error ->
                markFailed(
                    operation = operation,
                    error = error,
                    retryWork = error.shouldRetryWork(),
                )
            },
        )
    }

    private suspend fun syncUpdateTradingStations(operation: SyncOutboxEntity): OperationResult {
        return runCatching {
            configRepository.updateTradingStations()
        }.fold(
            onSuccess = {
                syncOutboxDao.delete(operation)
                OperationResult.Success
            },
            onFailure = { error ->
                markFailed(
                    operation = operation,
                    error = error,
                    retryWork = error.shouldRetryWork(),
                )
            },
        )
    }

    private suspend fun syncCurrentOrderStatus(retryWhenWaitingForNetwork: Boolean): OperationResult {
        val order = ordersDao.getLatestOrderByStatusesOnce(ACTIVE_ORDER_STATUSES) ?: return OperationResult.Success
        val serverOrderId = order.order.serverId ?: return OperationResult.Success
        val localOrderId = order.order.id
        val lastUpdated = orderHistorySyncStore.getCurrentOrderStatusLastUpdated(serverOrderId)
        val existingStatuses = order.statusHistory.map { it.status.name }.toSet()

        if (!retryWhenWaitingForNetwork && !networkStatusObserver.isConnected()) {
            markCurrentOrderStatusWaitingForNetwork(localOrderId)
            return OperationResult(
                success = false,
                retryWork = retryWhenWaitingForNetwork,
            )
        }

        ordersDao.updateNetworkStatus(
            localOrderId = localOrderId,
            networkStatus = OrderNetworkStatusEntity.Updating,
        )

        return runCatching {
            remoteOrdersDataSource.checkCurrentOrderStatus(lastUpdated = lastUpdated)
        }.fold(
            onSuccess = { updates ->
                val remoteLastUpdated = if (updates.orderId == serverOrderId) {
                    updates.statusHistory.maxOfOrNull { it.time }
                } else {
                    null
                }
                val statusUpdates = updates.statusHistory.filter { update ->
                    update.time > lastUpdated && update.status.name !in existingStatuses
                }

                database.withTransaction {
                    if (updates.orderId == serverOrderId && statusUpdates.isNotEmpty()) {
                        val latestStatus = statusUpdates.maxBy { it.time }.status

                        ordersDao.updateStatus(
                            localOrderId = localOrderId,
                            status = OrderStatusEntity.valueOf(latestStatus.name),
                        )
                        ordersDao.insertStatusHistory(
                            statusUpdates.map { update ->
                                OrderStatusHistoryEntity(
                                    orderId = localOrderId,
                                    status = OrderStatusEntity.valueOf(update.status.name),
                                    time = update.time,
                                    comment = update.comment,
                                )
                            },
                        )
                    }
                    ordersDao.updateNetworkStatus(
                        localOrderId = localOrderId,
                        networkStatus = null,
                    )
                }
                if (remoteLastUpdated != null) {
                    orderHistorySyncStore.setCurrentOrderStatusLastUpdated(
                        orderId = serverOrderId,
                        lastUpdated = remoteLastUpdated,
                    )
                }
                OperationResult.Success
            },
            onFailure = { error ->
                markCurrentOrderStatusFailed(
                    localOrderId = localOrderId,
                    retryWork = error.shouldRetryWork(),
                )
            },
        )
    }

    private suspend fun completeMissingLocalOrder(operation: SyncOutboxEntity): OperationResult {
        syncOutboxDao.delete(operation)
        return OperationResult.Success
    }

    private suspend fun deleteLocalOrder(localOrderId: Int) {
        ordersDao.deleteOrderProducts(localOrderId)
        ordersDao.deleteStatusHistory(localOrderId)
        ordersDao.deleteOrder(localOrderId)
    }

    private suspend fun resetStaleRunningOperations() {
        database.withTransaction {
            syncOutboxDao.getRunningOperations().forEach { operation ->
                when (operation.operationType) {
                    SyncOperationTypeEntity.CreateOrderForNomad,
                    SyncOperationTypeEntity.CreateOrder -> ordersDao.updateNetworkStatus(
                        localOrderId = operation.localEntityId,
                        networkStatus = OrderNetworkStatusEntity.Loading,
                    )
                    SyncOperationTypeEntity.ChangeOrderStatus -> ordersDao.updateNetworkStatus(
                        localOrderId = operation.localEntityId,
                        networkStatus = OrderNetworkStatusEntity.Updating,
                    )
                    SyncOperationTypeEntity.UpdateCatalog,
                    SyncOperationTypeEntity.UpdateTradingStations,
                    -> Unit
                }
            }
            syncOutboxDao.resetRunningOperations()
        }
    }

    private suspend fun hasOutboxOperations(): Boolean {
        return syncOutboxDao.getOperationCount(OUTBOX_STATUSES) > 0
    }

    private suspend fun markReadyOperationsWaitingForNetwork(): Boolean {
        val now = System.currentTimeMillis()

        return database.withTransaction {
            val operations = syncOutboxDao.getReadyOperations(
                now = now,
                statuses = READY_STATUSES,
            )

            operations.forEach { operation ->
                updateOrderWaitingForNetwork(operation)
            }

            operations.isNotEmpty()
        }
    }

    private suspend fun markCurrentOrderStatusWaitingForNetwork(): Boolean {
        return database.withTransaction {
            val order = ordersDao.getLatestOrderByStatusesOnce(ACTIVE_ORDER_STATUSES)
            val localOrderId = order?.order?.id

            if (localOrderId != null && order.order.serverId != null) {
                markCurrentOrderStatusWaitingForNetwork(localOrderId)
                true
            } else {
                false
            }
        }
    }

    private suspend fun markCurrentOrderStatusWaitingForNetwork(localOrderId: Int) {
        ordersDao.updateNetworkStatus(
            localOrderId = localOrderId,
            networkStatus = OrderNetworkStatusEntity.UpdateFailed,
        )
    }

    private suspend fun markWaitingForNetwork(operation: SyncOutboxEntity) {
        database.withTransaction {
            updateOrderWaitingForNetwork(operation)
            syncOutboxDao.updateStatus(
                operationId = operation.id,
                status = SyncOutboxStatusEntity.Pending,
            )
        }
    }

    private suspend fun updateOrderWaitingForNetwork(operation: SyncOutboxEntity) {
        if (!operation.operationType.isOrderOperation()) {
            return
        }

        ordersDao.updateNetworkStatus(
            localOrderId = operation.localEntityId,
            networkStatus = when (operation.operationType) {
                SyncOperationTypeEntity.CreateOrderForNomad,
                SyncOperationTypeEntity.CreateOrder -> OrderNetworkStatusEntity.Failed
                SyncOperationTypeEntity.ChangeOrderStatus -> OrderNetworkStatusEntity.UpdateFailed
                SyncOperationTypeEntity.UpdateCatalog,
                SyncOperationTypeEntity.UpdateTradingStations,
                -> error("Catalog refresh operations do not have order network status")
            },
        )
    }

    private suspend fun markCurrentOrderStatusFailed(
        localOrderId: Int,
        retryWork: Boolean,
    ): OperationResult {
        ordersDao.updateNetworkStatus(
            localOrderId = localOrderId,
            networkStatus = OrderNetworkStatusEntity.UpdateFailed,
        )

        return OperationResult(
            success = false,
            retryWork = retryWork,
        )
    }

    private suspend fun markFailed(
        operation: SyncOutboxEntity,
        error: Throwable,
        retryWork: Boolean,
    ): OperationResult {
        database.withTransaction {
            updateOrderWaitingForNetwork(operation)
            syncOutboxDao.markFailed(
                operationId = operation.id,
                nextAttemptAt = System.currentTimeMillis(),
                lastError = error.message,
            )
        }

        return OperationResult(
            success = false,
            retryWork = retryWork,
        )
    }

    private fun Throwable.shouldRetryWork(): Boolean {
        return when (this) {
            is IOException -> true
            is ClientRequestException -> response.status == HttpStatusCode.TooManyRequests
            is ResponseException -> response.status.value >= HTTP_SERVER_ERROR_MIN
            else -> false
        }
    }

    private fun SyncOperationTypeEntity.isOrderOperation(): Boolean {
        return when (this) {
            SyncOperationTypeEntity.CreateOrderForNomad,
            SyncOperationTypeEntity.CreateOrder,
            SyncOperationTypeEntity.ChangeOrderStatus,
            -> true
            SyncOperationTypeEntity.UpdateCatalog,
            SyncOperationTypeEntity.UpdateTradingStations,
            -> false
        }
    }

    data class SyncResult(
        val shouldRetry: Boolean,
    )

    private data class OperationResult(
        val success: Boolean,
        val retryWork: Boolean,
    ) {

        companion object {

            val Success = OperationResult(
                success = true,
                retryWork = false,
            )
        }
    }

    private companion object {

        const val HTTP_SERVER_ERROR_MIN = 500

        val READY_STATUSES = listOf(
            SyncOutboxStatusEntity.Pending,
            SyncOutboxStatusEntity.Failed,
        )

        val OUTBOX_STATUSES = listOf(
            SyncOutboxStatusEntity.Pending,
            SyncOutboxStatusEntity.Running,
            SyncOutboxStatusEntity.Failed,
        )

        val ACTIVE_ORDER_STATUSES = listOf(
            OrderStatusEntity.Created,
            OrderStatusEntity.Processing,
            OrderStatusEntity.Sent,
        )

        val mutex = Mutex()
    }
}
