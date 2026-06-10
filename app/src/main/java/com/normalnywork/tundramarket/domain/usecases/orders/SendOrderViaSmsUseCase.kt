package com.normalnywork.tundramarket.domain.usecases.orders

import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.repositories.OrderRepository
import org.koin.core.annotation.Singleton

@Singleton
class SendOrderViaSmsUseCase(private val repository: OrderRepository) {

    suspend operator fun invoke(
        order: Order,
        comment: String,
    ) {
        repository.sendOrderViaSms(
            order = order,
            comment = comment,
        )
    }
}
