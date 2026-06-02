package com.normalnywork.tundramarket.domain.usecases.orders

import com.normalnywork.tundramarket.domain.repositories.OrderRepository
import org.koin.core.annotation.Singleton

@Singleton
class GetCurrentOrderUseCase(private val repository: OrderRepository) {

    operator fun invoke() = repository.getCurrentOrder()
}
