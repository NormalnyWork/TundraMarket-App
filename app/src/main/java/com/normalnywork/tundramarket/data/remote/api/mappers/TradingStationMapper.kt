package com.normalnywork.tundramarket.data.remote.api.mappers

import com.normalnywork.tundramarket.data.remote.api.proto.ProtoTradingStation
import com.normalnywork.tundramarket.domain.entities.TradingStation

fun ProtoTradingStation.toDomain() = TradingStation(
    id = id,
    name = name,
    phone = phone,
    location = location.toDomain(),
)