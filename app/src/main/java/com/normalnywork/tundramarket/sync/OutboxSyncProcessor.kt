package com.normalnywork.tundramarket.sync

import androidx.room.withTransaction
import com.normalnywork.tundramarket.data.local.db.TMDatabase
import com.normalnywork.tundramarket.data.local.db.dao.OrdersDao
import com.normalnywork.tundramarket.data.local.db.dao.SyncOutboxDao
import com.normalnywork.tundramarket.data.local.db.entities.OrderNetworkStatusEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOperationTypeEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOutboxEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOutboxStatusEntity
import com.normalnywork.tundramarket.data.local.db.mappers.toDomain
import com.normalnywork.tundramarket.data.remote.source.RemoteOrdersDataSource
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
) {

    suspend fun syncPendingOperations(): SyncResult = mutex.withLock {
        resetStaleRunningOperations()

        var shouldRetry = false

        while (true) {
            val operation = claimNextOperation() ?: break
            val operationResult = syncOperation(operation)

            if (!operationResult.success) {
                shouldRetry = operationResult.retryWork
                break
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

    private suspend fun syncOperation(operation: SyncOutboxEntity): OperationResult {
        return when (operation.operationType) {
            SyncOperationTypeEntity.CreateOrder -> syncCreateOrder(operation)
        }
    }

    private suspend fun syncCreateOrder(operation: SyncOutboxEntity): OperationResult {
        val order = ordersDao.getOrderByLocalId(operation.localEntityId)
            ?: return completeMissingLocalOrder(operation)

        ordersDao.updateNetworkStatus(
            localOrderId = operation.localEntityId,
            networkStatus = OrderNetworkStatusEntity.Processing,
        )

        return runCatching {
            remoteOrdersDataSource.createOrder(
                order = order.toDomain(),
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

    private suspend fun completeMissingLocalOrder(operation: SyncOutboxEntity): OperationResult {
        syncOutboxDao.delete(operation)
        return OperationResult.Success
    }

    private suspend fun resetStaleRunningOperations() {
        database.withTransaction {
            syncOutboxDao.getRunningOperations().forEach { operation ->
                when (operation.operationType) {
                    SyncOperationTypeEntity.CreateOrder -> ordersDao.updateNetworkStatus(
                        localOrderId = operation.localEntityId,
                        networkStatus = OrderNetworkStatusEntity.Enqueued,
                    )
                }
            }
            syncOutboxDao.resetRunningOperations()
        }
    }

    private suspend fun markFailed(
        operation: SyncOutboxEntity,
        error: Throwable,
        retryWork: Boolean,
    ): OperationResult {
        database.withTransaction {
            ordersDao.updateNetworkStatus(
                localOrderId = operation.localEntityId,
                networkStatus = OrderNetworkStatusEntity.Failed,
            )
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

        val mutex = Mutex()
    }
}
