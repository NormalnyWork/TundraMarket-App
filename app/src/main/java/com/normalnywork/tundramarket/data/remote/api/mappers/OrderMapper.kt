package com.normalnywork.tundramarket.data.remote.api.mappers

import com.normalnywork.tundramarket.data.remote.api.proto.CreateOrderRequest
import com.normalnywork.tundramarket.data.remote.api.proto.ProtoProductCount
import com.normalnywork.tundramarket.domain.entities.Order

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
