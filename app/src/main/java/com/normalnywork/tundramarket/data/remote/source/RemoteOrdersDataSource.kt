package com.normalnywork.tundramarket.data.remote.source

import com.normalnywork.tundramarket.domain.entities.Location
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

    suspend fun getCurrentOrder(): OrderListItem?

    suspend fun getHistoryOrders(
        anchor: Int?,
        pageSize: Int,
    ): OrderListPage

    data class OrderStatusUpdates(
        val orderId: Int,
        val statusHistory: List<OrderStatusHistory>,
    )

    data class OrderListPage(
        val orders: List<OrderListItem>,
    )

    data class OrderListItem(
        val id: Int,
        val nomadId: Int,
        val tradingStationId: Int,
        val status: OrderStatus,
        val statusHistory: List<OrderStatusHistory>,
        val comment: String,
        val cart: List<ProductCount>,
        val location: Location,
    )

    data class ProductCount(
        val productId: Int,
        val count: Int,
    )
}
