package com.normalnywork.tundramarket.domain.usecases.orders

import com.normalnywork.tundramarket.domain.repositories.OrderRepository
import org.koin.core.annotation.Singleton

@Singleton
class InitializeCurrentOrderUseCase(private val repository: OrderRepository) {

    suspend operator fun invoke() {
        repository.initializeCurrentOrder()
    }
}
