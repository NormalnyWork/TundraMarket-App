package com.normalnywork.tundramarket.data.remote.api.mappers

import com.normalnywork.tundramarket.data.remote.api.proto.ProtoLocation
import com.normalnywork.tundramarket.domain.entities.Location

fun ProtoLocation.toDomain() = Location(
    latitude = latitude,
    longitude = longitude,
)