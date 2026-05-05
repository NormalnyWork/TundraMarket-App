package com.normalnywork.tundramarket.data.remote.api.mappers

import com.normalnywork.tundramarket.data.remote.api.proto.ProtoProduct
import com.normalnywork.tundramarket.domain.entities.Product

fun ProtoProduct.toDomain() = Product(
    id = id,
    name = name,
    details = details,
    weight = weight.toFloat(),
    volume = volume.toFloat(),
)
