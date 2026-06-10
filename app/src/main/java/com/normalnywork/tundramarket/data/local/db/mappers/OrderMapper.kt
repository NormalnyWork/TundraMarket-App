package com.normalnywork.tundramarket.data.local.db.mappers

import com.normalnywork.tundramarket.data.local.db.entities.OrderEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderNetworkStatusEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderProductEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusEntity
import com.normalnywork.tundramarket.data.local.db.entities.OrderStatusHistoryEntity
import com.normalnywork.tundramarket.data.local.db.relations.OrderProductWithProduct
import com.normalnywork.tundramarket.data.local.db.relations.OrderWithDetails
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderNetworkStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatusHistory
import com.normalnywork.tundramarket.domain.entities.Product

fun OrderWithDetails.toDomain() = Order(
    id = order.serverId ?: order.id,
    nomadId = order.nomadId,
    tradingStation = tradingStation.toDomain(),
    cart = cart.map { it.toDomain() },
    assembledProductIds = cart
        .filter { it.orderProduct.isAssembled }
        .map { it.orderProduct.productId }
        .toSet(),
    location = order.location.toDomain(),
    comment = order.comment,
    status = order.status.toDomain(),
    statusHistory = statusHistory
        .sortedBy { it.time }
        .map { it.toDomain() },
    networkStatus = order.networkStatus?.toDomain(),
    isCreatedViaSms = order.isCreatedViaSms,
)

fun Order.toEntity(
    id: Int,
    createdAt: Long,
    serverId: Int? = null,
) = OrderEntity(
    id = id,
    serverId = serverId,
    nomadId = nomadId,
    tradingStationId = tradingStation.id,
    location = location.toEntity(),
    comment = comment,
    status = status.toEntity(),
    networkStatus = networkStatus?.toEntity(),
    isCreatedViaSms = isCreatedViaSms,
    createdAt = createdAt,
)

fun Pair<Product, Int>.toOrderProductEntity(orderId: Int) = OrderProductEntity(
    orderId = orderId,
    productId = first.id,
    count = second,
)

fun OrderStatusHistory.toEntity(orderId: Int) = OrderStatusHistoryEntity(
    orderId = orderId,
    status = status.toEntity(),
    time = time,
    comment = comment,
)

private fun OrderProductWithProduct.toDomain() = product.toDomain() to orderProduct.count

private fun OrderStatusHistoryEntity.toDomain() = OrderStatusHistory(
    status = status.toDomain(),
    time = time,
    comment = comment,
)

private fun OrderStatusEntity.toDomain() = OrderStatus.valueOf(name)

private fun OrderStatus.toEntity() = OrderStatusEntity.valueOf(name)

private fun OrderNetworkStatusEntity.toDomain() = OrderNetworkStatus.valueOf(name)

private fun OrderNetworkStatus.toEntity() = OrderNetworkStatusEntity.valueOf(name)
