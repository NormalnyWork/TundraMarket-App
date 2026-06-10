package com.normalnywork.tundramarket.data.remote.api.mappers

import com.normalnywork.tundramarket.data.remote.api.proto.ChangeOrderStatusRequest
import com.normalnywork.tundramarket.data.remote.api.proto.CheckOrderStatusResponse
import com.normalnywork.tundramarket.data.remote.api.proto.CreateOrderRequest
import com.normalnywork.tundramarket.data.remote.api.proto.OrderListResponse
import com.normalnywork.tundramarket.data.remote.api.proto.OrderResponse
import com.normalnywork.tundramarket.data.remote.api.proto.ProtoOrder
import com.normalnywork.tundramarket.data.remote.api.proto.ProtoOrderStatus
import com.normalnywork.tundramarket.data.remote.api.proto.ProtoOrderStatusHistory
import com.normalnywork.tundramarket.data.remote.api.proto.ProtoProductCount
import com.normalnywork.tundramarket.data.remote.source.RemoteOrdersDataSource
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatusHistory

fun Long.unixMillisecondsToSeconds() = this / MILLIS_IN_SECOND

fun Long.unixSecondsToMilliseconds() = this * MILLIS_IN_SECOND

fun Order.toCreateRequest() = CreateOrderRequest(
    nomadId = nomadId.takeIf { it > 0 },
    tradingStationId = tradingStation.id,
    location = location.toProto(),
    cart = cart.map { (product, count) ->
        ProtoProductCount(
            productId = product.id,
            count = count,
        )
    },
    comment = comment,
)

fun OrderStatus.toChangeStatusRequest(
    orderId: Int,
    comment: String? = null,
) = ChangeOrderStatusRequest(
    orderId = orderId,
    newStatus = toProto(),
    comment = comment,
)

fun CheckOrderStatusResponse.toOrderStatusUpdates() = RemoteOrdersDataSource.OrderStatusUpdates(
    orderId = orderId,
    statusHistory = statusUpdates.map { it.toDomain() },
)

fun OrderResponse.toOrderListItem() = order?.toOrderListItem()

fun OrderListResponse.toOrderListPage() = RemoteOrdersDataSource.OrderListPage(
    orders = orders.map { it.toOrderListItem() },
)

private fun ProtoOrder.toOrderListItem() = RemoteOrdersDataSource.OrderListItem(
    id = id,
    nomadId = nomadId,
    tradingStationId = tradingStationId,
    status = status.toDomain(),
    statusHistory = history.map { it.toDomain() },
    comment = comment,
    cart = card.map { productCount ->
        RemoteOrdersDataSource.ProductCount(
            productId = productCount.productId,
            count = productCount.count,
        )
    },
    location = location.toDomain(),
)

private fun ProtoOrderStatusHistory.toDomain() = OrderStatusHistory(
    status = status.toDomain(),
    time = time.unixSecondsToMilliseconds(),
    comment = comment,
)

private fun ProtoOrderStatus.toDomain() = when (this) {
    ProtoOrderStatus.CREATED -> OrderStatus.Created
    ProtoOrderStatus.PROCESSING -> OrderStatus.Processing
    ProtoOrderStatus.SENT -> OrderStatus.Sent
    ProtoOrderStatus.COMPLETED -> OrderStatus.Completed
    ProtoOrderStatus.CANCELLED -> OrderStatus.Cancelled
    ProtoOrderStatus.DENIED -> OrderStatus.Denied
}

private fun OrderStatus.toProto() = when (this) {
    OrderStatus.Created -> ProtoOrderStatus.CREATED
    OrderStatus.Processing -> ProtoOrderStatus.PROCESSING
    OrderStatus.Sent -> ProtoOrderStatus.SENT
    OrderStatus.Completed -> ProtoOrderStatus.COMPLETED
    OrderStatus.Cancelled -> ProtoOrderStatus.CANCELLED
    OrderStatus.Denied -> ProtoOrderStatus.DENIED
}

private const val MILLIS_IN_SECOND = 1_000L
