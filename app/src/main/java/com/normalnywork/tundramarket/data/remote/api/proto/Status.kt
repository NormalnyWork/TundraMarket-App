@file:OptIn(ExperimentalSerializationApi::class)

package com.normalnywork.tundramarket.data.remote.api.proto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
enum class ProtoOrderStatus {
    @ProtoNumber(0) CREATED,
    @ProtoNumber(1) PROCESSING,
    @ProtoNumber(2) SENT,
    @ProtoNumber(3) COMPLETED,
    @ProtoNumber(4) CANCELLED,
    @ProtoNumber(5) DENIED,
}

@Serializable
data class ProtoOrderStatusHistory(
    @ProtoNumber(1) val status: ProtoOrderStatus,
    @ProtoNumber(2) val time: Long,
)