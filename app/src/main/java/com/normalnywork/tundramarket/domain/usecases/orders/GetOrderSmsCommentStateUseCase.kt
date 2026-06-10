package com.normalnywork.tundramarket.domain.usecases.orders

import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.repositories.OrderRepository
import org.koin.core.annotation.Singleton

@Singleton
class GetOrderSmsCommentStateUseCase(private val repository: OrderRepository) {

    operator fun invoke(
        order: Order,
        comment: String,
    ) = repository.getOrderSmsCommentState(
        order = order,
        comment = comment,
    )
}
