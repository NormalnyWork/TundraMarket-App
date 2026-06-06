package com.normalnywork.tundramarket.data.remote.api.mappers

import com.normalnywork.tundramarket.data.remote.api.proto.ChangeOrderStatusRequest
import com.normalnywork.tundramarket.data.remote.api.proto.CheckOrderStatusResponse
import com.normalnywork.tundramarket.data.remote.api.proto.CreateOrderRequest
import com.normalnywork.tundramarket.data.remote.api.proto.OrderListResponse
import com.normalnywork.tundramarket.data.remote.api.proto.ProtoOrderStatus
import com.normalnywork.tundramarket.data.remote.api.proto.ProtoOrderStatusHistory
import com.normalnywork.tundramarket.data.remote.api.proto.ProtoProductCount
import com.normalnywork.tundramarket.data.remote.source.RemoteOrdersDataSource
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatusHistory

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

fun OrderStatus.toChangeStatusRequest(orderId: Int) = ChangeOrderStatusRequest(
    orderId = orderId,
    newStatus = toProto(),
)

fun CheckOrderStatusResponse.toOrderStatusUpdates() = RemoteOrdersDataSource.OrderStatusUpdates(
    orderId = orderId,
    statusHistory = statusUpdates.map { it.toDomain() },
)

fun OrderListResponse.toOrderListPage() = RemoteOrdersDataSource.OrderListPage(
    orders = orders.map { order ->
        RemoteOrdersDataSource.OrderListItem(
            id = order.id,
            nomadId = order.nomadId,
            tradingStationId = order.tradingStationId,
            status = order.status.toDomain(),
            statusHistory = order.history.map { it.toDomain() },
            comment = order.comment,
            cart = order.card.map { productCount ->
                RemoteOrdersDataSource.ProductCount(
                    productId = productCount.productId,
                    count = productCount.count,
                )
            },
            location = order.location.toDomain(),
        )
    },
)

private fun ProtoOrderStatusHistory.toDomain() = OrderStatusHistory(
    status = status.toDomain(),
    time = time,
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
