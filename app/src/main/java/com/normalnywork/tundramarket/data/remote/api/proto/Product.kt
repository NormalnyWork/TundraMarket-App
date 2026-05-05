@file:OptIn(ExperimentalSerializationApi::class)

package com.normalnywork.tundramarket.data.remote.api.proto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * @param weight in grams
 * @param volume in milliliters
 */
@Serializable
data class ProtoProduct(
    @ProtoNumber(1) val id: Int,
    @ProtoNumber(2) val name: String,
    @ProtoNumber(3) val details: String? = null,
    @ProtoNumber(4) val weight: Int,
    @ProtoNumber(5) val volume: Int,
)

@Serializable
data class ProtoProductCount(
    @ProtoNumber(1) val productId: Int,
    @ProtoNumber(2) val count: Int,
)

@Serializable
data class CatalogResponse(
    @ProtoNumber(1) val products: List<ProtoProduct> = emptyList(),
)