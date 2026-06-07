package com.normalnywork.tundramarket.domain.entities

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus {
    Created,
    Processing,
    Sent,
    Completed,
    Cancelled,
    Denied
}

val OrderStatus.isTerminal: Boolean
    get() = this == OrderStatus.Completed ||
            this == OrderStatus.Cancelled ||
            this == OrderStatus.Denied