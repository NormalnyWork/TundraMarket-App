package com.normalnywork.tundramarket.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class TradingStation(
    val id: Int,
    val name: String,
    val phone: String?,
    val location: Location,
)
