package com.normalnywork.tundramarket.data.remote.sms

import com.normalnywork.tundramarket.domain.entities.Location

data class TmSmsOrder(
    val clientOrderId: Int,
    val location: Location,
    val cart: List<TmSmsOrderCartItem>,
    val comment: String? = null,
)

data class TmSmsOrderCartItem(
    val productId: Int,
    val quantity: Int,
)
