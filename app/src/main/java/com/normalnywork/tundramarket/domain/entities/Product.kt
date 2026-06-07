package com.normalnywork.tundramarket.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val details: String?,
    val weight: Float,
    val volume: Float,
)
