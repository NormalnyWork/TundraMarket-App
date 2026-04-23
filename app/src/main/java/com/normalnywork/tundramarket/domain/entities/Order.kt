package com.normalnywork.tundramarket.domain.entities

data class Order(
    val id: Int,
    val nomadId: Int,
    val tradingStation: TradingStation,
    val cart: List<Pair<Product, Int>>,
    val location: Location,
    val comment: String,
    val status: OrderStatus,
    val statusHistory: List<OrderStatusHistory>,
)
