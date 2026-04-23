package com.normalnywork.tundramarket.domain.entities

data class TradingStation(
    val id: Int,
    val name: String,
    val phone: String?,
    val location: Location,
)
