package com.normalnywork.tundramarket.data.remote.source

import com.normalnywork.tundramarket.domain.entities.Product

interface RemoteProductsDataSource {

    suspend fun getCatalog(): List<Product>
}
