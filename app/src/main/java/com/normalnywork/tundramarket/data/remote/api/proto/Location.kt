@file:OptIn(ExperimentalSerializationApi::class)

package com.normalnywork.tundramarket.data.remote.api.proto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class ProtoLocation(
    @ProtoNumber(1) val longitude: Double,
    @ProtoNumber(2) val latitude: Double,
)
