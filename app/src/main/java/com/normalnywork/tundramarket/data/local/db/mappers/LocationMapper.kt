package com.normalnywork.tundramarket.data.local.db.mappers

import com.normalnywork.tundramarket.data.local.db.entities.LocationEntity
import com.normalnywork.tundramarket.domain.entities.Location

fun LocationEntity.toDomain() = Location(
    latitude = latitude,
    longitude = longitude,
)

fun Location.toEntity() = LocationEntity(
    latitude = latitude,
    longitude = longitude,
)