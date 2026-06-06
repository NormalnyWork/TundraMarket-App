package com.normalnywork.tundramarket.data.remote.source

import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatusHistory

interface RemoteOrdersDataSource {

    suspend fun createOrder(
        order: Order,
        idempotencyKey: String,
    ): Int

    suspend fun changeOrderStatus(
        orderId: Int,
        status: OrderStatus,
        idempotencyKey: String,
    ): Long

    suspend fun checkCurrentOrderStatus(lastUpdated: Long): OrderStatusUpdates

    data class OrderStatusUpdates(
        val orderId: Int,
        val statusHistory: List<OrderStatusHistory>,
    )
}
