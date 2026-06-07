package com.normalnywork.tundramarket.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusHistory(
    val status: OrderStatus,
    val time: Long,
)
