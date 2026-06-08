package com.normalnywork.tundramarket.domain.repositories

import androidx.paging.PagingData
import com.normalnywork.tundramarket.domain.entities.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    fun getCurrentOrder(): Flow<Order?>

    suspend fun initializeCurrentOrder()

    suspend fun createOrder(order: Order)

    suspend fun updateCurrentOrderStatus()

    suspend fun changeOrderStatus(order: Order)

    fun getProcessingOrders(): Flow<PagingData<Order>>

    fun getNewOrders(): Flow<PagingData<Order>>

    fun getHistoryOrders(): Flow<PagingData<Order>>

    fun getTradingStationHistoryOrders(): Flow<PagingData<Order>>

    fun hasNewOrders(): Flow<Boolean>

    suspend fun updateOrders()
}
