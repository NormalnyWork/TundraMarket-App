package com.normalnywork.tundramarket.data.local.db.mappers

import com.normalnywork.tundramarket.data.local.db.entities.TradingStationEntity
import com.normalnywork.tundramarket.domain.entities.TradingStation

fun TradingStationEntity.toDomain() = TradingStation(
    id = id,
    name = name,
    phone = phone,
    location = location.toDomain(),
)

fun TradingStation.toEntity() = TradingStationEntity(
    id = id,
    name = name,
    phone = phone,
    location = location.toEntity(),
)