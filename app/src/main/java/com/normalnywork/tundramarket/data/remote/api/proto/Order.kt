@file:OptIn(ExperimentalSerializationApi::class)

package com.normalnywork.tundramarket.data.remote.api.proto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class ProtoOrder(
    @ProtoNumber(1) val id: Int,
    @ProtoNumber(2) val nomadId: Int,
    @ProtoNumber(3) val tradingStationId: Int,
    @ProtoNumber(4) val status: ProtoOrderStatus,
    @ProtoNumber(5) val history: List<ProtoOrderStatusHistory> = emptyList(),
    @ProtoNumber(6) val comment: String = "",
    @ProtoNumber(7) val card: List<ProtoProductCount> = emptyList(),
    @ProtoNumber(8) val location: ProtoLocation,
)

@Serializable
data class OrderResponse(
    @ProtoNumber(1) val order: ProtoOrder? = null,
)

@Serializable
data class CreateOrderRequest(
    @ProtoNumber(1) val nomadId: Int? = null,
    @ProtoNumber(2) val tradingStationId: Int? = null,
    @ProtoNumber(3) val location: ProtoLocation,
    @ProtoNumber(4) val cart: List<ProtoProductCount>,
    @ProtoNumber(5) val comment: String,
)

@Serializable
data class CreateOrderResponse(
    @ProtoNumber(1) val orderId: Int,
)

@Serializable
data class CheckOrderStatusRequest(
    @ProtoNumber(1) val lastUpdated: Long,
)

@Serializable
data class CheckOrderStatusResponse(
    @ProtoNumber(1) val orderId: Int,
    @ProtoNumber(2) val statusUpdates: List<ProtoOrderStatusHistory> = emptyList(),
)

@Serializable
data class ChangeOrderStatusRequest(
    @ProtoNumber(1) val orderId: Int,
    @ProtoNumber(2) val newStatus: ProtoOrderStatus,
    @ProtoNumber(3) val comment: String? = null,
)

@Serializable
data class ChangeOrderStatusResponse(
    @ProtoNumber(1) val time: Long,
)

@Serializable
enum class ProtoOrderCategory {
    @ProtoNumber(0) NEW,
    @ProtoNumber(1) PROCESSING,
    @ProtoNumber(2) HISTORY,
}

@Serializable
data class OrderListRequest(
    @ProtoNumber(1) val anchor: Int? = null,
    @ProtoNumber(2) val pageSize: Int,
    @ProtoNumber(3) val orderCategory: ProtoOrderCategory,
)

@Serializable
data class OrderUpdatesRequest(
    @ProtoNumber(1) val lastUpdated: Long,
)

@Serializable
data class OrderListResponse(
    @ProtoNumber(1) val orders: List<ProtoOrder> = emptyList(),
)
