package com.normalnywork.tundramarket.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusHistory(
    val status: OrderStatus,
    val time: Long,
    val comment: String? = null,
)

val List<OrderStatusHistory>.denialComment: String?
    get() = filter { it.status == OrderStatus.Denied }
        .maxByOrNull { it.time }
        ?.comment
        ?.takeIf { it.isNotBlank() }
