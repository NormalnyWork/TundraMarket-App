package com.normalnywork.tundramarket.domain.usecases.orders

import com.normalnywork.tundramarket.domain.entities.Location
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatusHistory
import com.normalnywork.tundramarket.domain.entities.Product
import com.normalnywork.tundramarket.domain.entities.TradingStation
import com.normalnywork.tundramarket.domain.repositories.OrderRepository
import org.koin.core.annotation.Singleton

@Singleton
class CreateOrderUseCase(private val repository: OrderRepository) {

    suspend operator fun invoke(
        tradingStation: TradingStation,
        cart: List<Pair<Product, Int>>,
        location: Location,
        comment: String,
    ) {
        val now = System.currentTimeMillis()

        repository.createOrder(
            Order(
                id = TEMPORARY_ORDER_ID,
                nomadId = LOCAL_NOMAD_ID,
                tradingStation = tradingStation,
                cart = cart,
                location = location,
                comment = comment,
                status = OrderStatus.Created,
                statusHistory = listOf(
                    OrderStatusHistory(
                        status = OrderStatus.Created,
                        time = now,
                    ),
                ),
            ),
        )
    }

    private companion object {

        const val TEMPORARY_ORDER_ID = 0
        const val LOCAL_NOMAD_ID = 0
    }
}
