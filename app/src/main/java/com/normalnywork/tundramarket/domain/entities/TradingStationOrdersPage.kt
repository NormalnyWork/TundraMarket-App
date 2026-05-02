package com.normalnywork.tundramarket.domain.entities

import kotlinx.serialization.Serializable

@Serializable
enum class TradingStationOrdersPage {
    Active,
    New,
    History,
}
