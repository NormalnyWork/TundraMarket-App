package com.normalnywork.tundramarket.domain.entities

enum class OrderStatus {
    Created,
    Processing,
    Sent,
    Completed,
    Cancelled,
    Denied,
}