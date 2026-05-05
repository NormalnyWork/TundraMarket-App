package com.normalnywork.tundramarket.data.local.source

import com.normalnywork.tundramarket.domain.entities.Product
import kotlinx.coroutines.flow.Flow

interface LocalProductsDataSource {

    fun getProducts(): Flow<List<Product>>

    suspend fun updateProducts(newList: List<Product>)
}
