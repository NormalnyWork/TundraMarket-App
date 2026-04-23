package com.normalnywork.tundramarket.domain.entities

data class OrderStatusHistory(
    val status: OrderStatus,
    val time: Long,
)
