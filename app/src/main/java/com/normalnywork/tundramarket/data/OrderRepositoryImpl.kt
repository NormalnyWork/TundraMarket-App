package com.normalnywork.tundramarket.data

import androidx.paging.PagingData
import androidx.room.withTransaction
import com.normalnywork.tundramarket.data.local.db.TMDatabase
import com.normalnywork.tundramarket.data.local.db.dao.OrdersDao
import com.normalnywork.tundramarket.data.local.db.dao.SyncOutboxDao
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOperationTypeEntity
import com.normalnywork.tundramarket.data.local.db.entities.SyncOutboxEntity
import com.normalnywork.tundramarket.data.local.db.mappers.toDomain
import com.normalnywork.tundramarket.data.local.db.mappers.toEntity
import com.normalnywork.tundramarket.data.local.db.mappers.toOrderProductEntity
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderNetworkStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.repositories.OrderRepository
import com.normalnywork.tundramarket.sync.SyncWorkScheduler
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
    private val syncWorkScheduler: SyncWorkScheduler,
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
                networkStatus = OrderNetworkStatus.Enqueued,
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

    override suspend fun updateCurrentOrderStatus() = Unit

    override suspend fun changeOrderStatus(order: Order) {
        TODO("Order status changes will be implemented with the sync processor")
    }

    override fun getProcessingOrders(): Flow<PagingData<Order>> {
        return flowOf(PagingData.empty())
    }

    override fun getNewOrders(): Flow<PagingData<Order>> {
        return flowOf(PagingData.empty())
    }

    override fun getHistoryOrders(): Flow<PagingData<Order>> {
        return flowOf(PagingData.empty())
    }

    override suspend fun updateOrders() = Unit

    private companion object {

        val CURRENT_ORDER_STATUSES = listOf(
            OrderStatus.Created,
            OrderStatus.Processing,
            OrderStatus.Sent,
        ).map { OrderStatusEntity.valueOf(it.name) }
    }
}
