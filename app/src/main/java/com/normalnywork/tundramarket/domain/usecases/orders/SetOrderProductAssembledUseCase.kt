package com.normalnywork.tundramarket.domain.usecases.orders

import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.repositories.OrderRepository
import org.koin.core.annotation.Singleton

@Singleton
class SetOrderProductAssembledUseCase(private val repository: OrderRepository) {

    suspend operator fun invoke(
        order: Order,
        productId: Int,
        isAssembled: Boolean,
    ) {
        repository.setOrderProductAssembled(
            order = order,
            productId = productId,
            isAssembled = isAssembled,
        )
    }
}
