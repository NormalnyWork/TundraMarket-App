@file:OptIn(ExperimentalSerializationApi::class)

package com.normalnywork.tundramarket.data.remote.api.proto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class ProtoTradingStation(
    @ProtoNumber(1) val id: Int,
    @ProtoNumber(2) val name: String,
    @ProtoNumber(3) val phone: String? = null,
    @ProtoNumber(4) val location: ProtoLocation,
)

@Serializable
data class TradingStationsListResponse(
    @ProtoNumber(1) val tradingStations: List<ProtoTradingStation> = emptyList(),
)