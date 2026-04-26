@file:OptIn(ExperimentalSerializationApi::class)

package com.normalnywork.tundramarket.data.remote.proto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class AuthRequest(
    @ProtoNumber(1) val phone: String,
    @ProtoNumber(2) val tradingStationId: Int? = null,
)

@Serializable
data class AuthResponse(
    @ProtoNumber(1) val token: String,
)