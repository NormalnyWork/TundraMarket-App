package com.normalnywork.tundramarket.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int,
    val nomadId: Int,
    val tradingStation: TradingStation,
    val cart: List<Pair<Product, Int>>,
    val location: Location,
    val comment: String,
    val status: OrderStatus,
    val statusHistory: List<OrderStatusHistory>,
    val networkStatus: OrderNetworkStatus? = null,
)
