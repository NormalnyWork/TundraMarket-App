package com.normalnywork.tundramarket.domain.usecases.orders

import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.repositories.OrderRepository
import org.koin.core.annotation.Singleton

@Singleton
class GetOrderSmsSendStateUseCase(private val repository: OrderRepository) {

    operator fun invoke(order: Order) = repository.getOrderSmsSendState(order)
}
