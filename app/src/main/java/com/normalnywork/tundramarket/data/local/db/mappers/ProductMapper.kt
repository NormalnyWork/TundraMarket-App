package com.normalnywork.tundramarket.data.local.db.mappers

import com.normalnywork.tundramarket.data.local.db.entities.ProductEntity
import com.normalnywork.tundramarket.domain.entities.Product

fun ProductEntity.toDomain() = Product(
    id = id,
    name = name,
    details = details,
    weight = weight,
    volume = volume,
)

fun Product.toEntity() = ProductEntity(
    id = id,
    name = name,
    details = details,
    weight = weight,
    volume = volume,
)
