package com.normalnywork.tundramarket.domain.repositories

import androidx.paging.PagingData
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderSmsCommentState
import com.normalnywork.tundramarket.domain.entities.OrderSmsSendState
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    fun getCurrentOrder(): Flow<Order?>

    fun getOrder(orderId: Int): Flow<Order?>

    suspend fun initializeCurrentOrder()

    suspend fun createOrder(order: Order)

    suspend fun sendOrderViaSms(
        order: Order,
        comment: String,
    )

    fun getOrderSmsSendState(order: Order): OrderSmsSendState

    fun getOrderSmsCommentState(
        order: Order,
        comment: String,
    ): OrderSmsCommentState

    suspend fun updateCurrentOrderStatus()

    suspend fun changeOrderStatus(
        order: Order,
        comment: String? = null,
    )

    suspend fun setOrderProductAssembled(
        order: Order,
        productId: Int,
        isAssembled: Boolean,
    )

    fun getProcessingOrders(): Flow<PagingData<Order>>

    fun getNewOrders(): Flow<PagingData<Order>>

    fun getHistoryOrders(): Flow<PagingData<Order>>

    fun getTradingStationHistoryOrders(): Flow<PagingData<Order>>

    fun hasNewOrders(): Flow<Boolean>

    suspend fun updateOrders()
}
