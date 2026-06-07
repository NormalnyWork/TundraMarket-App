package com.normalnywork.tundramarket.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val latitude: Float,
    val longitude: Float,
)
