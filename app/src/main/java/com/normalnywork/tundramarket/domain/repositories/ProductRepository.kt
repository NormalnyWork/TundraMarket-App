package com.normalnywork.tundramarket.domain.repositories

import com.normalnywork.tundramarket.domain.entities.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun getCatalog(): Flow<List<Product>>

    suspend fun updateCatalog()
}
