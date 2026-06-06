package com.normalnywork.tundramarket.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.normalnywork.tundramarket.data.local.db.TMDatabase
import com.normalnywork.tundramarket.data.local.db.dao.OrdersDao
import com.normalnywork.tundramarket.data.local.db.dao.SyncOutboxDao
import com.normalnywork.tundramarket.data.local.db.dao.TradingStationsDao
import com.normalnywork.tundramarket.data.local.db.entities.OrderNetworkStatusEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusHistoryEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOperationTypeEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOutboxEntity
import com.normalnywork.tundramarket.data.local.db.mappers.toDomain
import com.normalnywork.tundramarket.data.local.db.mappers.toEntity
import com.normalnywork.tundramarket.data.local.db.mappers.toOrderProductEntity
import com.normalnywork.tundramarket.data.local.preferences.OrderHistorySyncStore
import com.normalnywork.tundramarket.data.remote.source.RemoteOrdersDataSource
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderNetworkStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.repositories.OrderRepository
import com.normalnywork.tundramarket.sync.SyncWorkScheduler
import com.normalnywork.tundramarket.utils.NetworkStatusObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton
import java.util.UUID

@Singleton
class OrderRepositoryImpl(
    private val database: TMDatabase,
    private val ordersDao: OrdersDao,
    private val syncOutboxDao: SyncOutboxDao,
    private val tradingStationsDao: TradingStationsDao,
    private val remoteOrdersDataSource: RemoteOrdersDataSource,
    private val orderHistorySyncStore: OrderHistorySyncStore,
    private val syncWorkScheduler: SyncWorkScheduler,
    private val networkStatusObserver: NetworkStatusObserver,
) : OrderRepository {

    override fun getCurrentOrder(): Flow<Order?> {
        return ordersDao.getLatestOrderByStatuses(CURRENT_ORDER_STATUSES)
            .map { orderWithDetails -> orderWithDetails?.toDomain() }
    }

    override suspend fun createOrder(order: Order) {
        val now = System.currentTimeMillis()

        database.withTransaction {
            val localOrder = order.copy(
                id = 0,
                networkStatus = if (networkStatusObserver.isConnected()) {
                    OrderNetworkStatus.Loading
                } else {
                    OrderNetworkStatus.Failed
                },
            )
            val localOrderId = ordersDao.insertOrder(
                localOrder.toEntity(
                    id = 0,
                    createdAt = now,
                ),
            ).toInt()

            ordersDao.insertOrderProducts(
                localOrder.cart.map { it.toOrderProductEntity(orderId = localOrderId) },
            )
            ordersDao.insertStatusHistory(
                localOrder.statusHistory.map { it.toEntity(orderId = localOrderId) },
            )
            syncOutboxDao.insert(
                SyncOutboxEntity(
                    operationType = SyncOperationTypeEntity.CreateOrder,
                    localEntityId = localOrderId,
                    nextAttemptAt = now,
                    createdAt = now,
                    idempotencyKey = UUID.randomUUID().toString(),
                ),
            )
        }

        syncWorkScheduler.schedule()
    }

    override suspend fun updateCurrentOrderStatus() {
        syncWorkScheduler.schedule(
            replace = true,
            syncCurrentOrderStatus = true,
        )
    }

    override suspend fun changeOrderStatus(order: Order) {
        val localOrder = ordersDao.getOrderByLocalOrServerId(order.id) ?: return
        val localOrderId = localOrder.order.id
        val status = OrderStatusEntity.valueOf(order.status.name)
        val now = System.currentTimeMillis()
        var shouldScheduleSync = true

        database.withTransaction {
            if (localOrder.order.serverId == null && status == OrderStatusEntity.Cancelled) {
                ordersDao.updateStatus(
                    localOrderId = localOrderId,
                    status = status,
                )
                ordersDao.updateNetworkStatus(
                    localOrderId = localOrderId,
                    networkStatus = null,
                )
                ordersDao.insertStatusHistory(
                    listOf(
                        OrderStatusHistoryEntity(
                            orderId = localOrderId,
                            status = status,
                            time = now,
                        ),
                    ),
                )
                syncOutboxDao.deleteByLocalEntityId(localOrderId)
                shouldScheduleSync = false
                return@withTransaction
            }

            ordersDao.updateStatus(
                localOrderId = localOrderId,
                status = status,
            )
            ordersDao.updateNetworkStatus(
                localOrderId = localOrderId,
                networkStatus = if (networkStatusObserver.isConnected()) {
                    OrderNetworkStatusEntity.Updating
                } else {
                    OrderNetworkStatusEntity.UpdateFailed
                },
            )
            ordersDao.insertStatusHistory(
                listOf(
                    OrderStatusHistoryEntity(
                        orderId = localOrderId,
                        status = status,
                        time = now,
                    ),
                ),
            )
            syncOutboxDao.insert(
                SyncOutboxEntity(
                    operationType = SyncOperationTypeEntity.ChangeOrderStatus,
                    localEntityId = localOrderId,
                    nextAttemptAt = now,
                    createdAt = now,
                    idempotencyKey = UUID.randomUUID().toString(),
                ),
            )
        }

        if (shouldScheduleSync) {
            syncWorkScheduler.schedule()
        }
    }

    override fun getProcessingOrders(): Flow<PagingData<Order>> {
        return flowOf(PagingData.empty())
    }

    override fun getNewOrders(): Flow<PagingData<Order>> {
        return flowOf(PagingData.empty())
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getHistoryOrders(): Flow<PagingData<Order>> {
        return Pager(
            config = PagingConfig(
                pageSize = HISTORY_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            remoteMediator = NomadHistoryRemoteMediator(
                database = database,
                ordersDao = ordersDao,
                tradingStationsDao = tradingStationsDao,
                remoteOrdersDataSource = remoteOrdersDataSource,
                orderHistorySyncStore = orderHistorySyncStore,
                networkStatusObserver = networkStatusObserver,
                historyOrderStatuses = HISTORY_ORDER_STATUSES,
                pageSize = HISTORY_PAGE_SIZE,
            ),
            pagingSourceFactory = {
                ordersDao.getOrdersByStatusesPaged(HISTORY_ORDER_STATUSES)
            },
        ).flow.map { pagingData ->
            pagingData.map { orderWithDetails -> orderWithDetails.toDomain() }
        }
    }

    override suspend fun updateOrders() = Unit

    private companion object {

        const val HISTORY_PAGE_SIZE = 20

        val CURRENT_ORDER_STATUSES = listOf(
            OrderStatus.Created,
            OrderStatus.Processing,
            OrderStatus.Sent,
            OrderStatus.Completed,
            OrderStatus.Cancelled,
            OrderStatus.Denied,
        ).map { OrderStatusEntity.valueOf(it.name) }

        val HISTORY_ORDER_STATUSES = listOf(
            OrderStatus.Completed,
            OrderStatus.Cancelled,
            OrderStatus.Denied,
        ).map { OrderStatusEntity.valueOf(it.name) }
    }
}
