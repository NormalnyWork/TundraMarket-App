package com.normalnywork.tundramarket.domain.usecases.orders

import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.repositories.OrderRepository
import org.koin.core.annotation.Singleton

@Singleton
class ChangeOrderStatusUseCase(private val repository: OrderRepository) {

    suspend operator fun invoke(
        order: Order,
        status: OrderStatus,
    ) {
        repository.changeOrderStatus(order.copy(status = status))
    }
}
