package com.normalnywork.tundramarket.domain.usecases.orders

import com.normalnywork.tundramarket.domain.repositories.OrderRepository
import org.koin.core.annotation.Singleton

@Singleton
class UpdateOrdersUseCase(private val repository: OrderRepository) {

    suspend operator fun invoke() = repository.updateOrders()
}
