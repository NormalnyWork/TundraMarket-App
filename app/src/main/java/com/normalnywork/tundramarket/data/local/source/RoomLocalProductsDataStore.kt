package com.normalnywork.tundramarket.data.local.source

import com.normalnywork.tundramarket.data.local.db.dao.ProductsDao
import com.normalnywork.tundramarket.data.local.db.mappers.toDomain
import com.normalnywork.tundramarket.data.local.db.mappers.toEntity
import com.normalnywork.tundramarket.domain.entities.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

@Singleton
class RoomLocalProductsDataStore(
    private val productsDao: ProductsDao,
) : LocalProductsDataSource {

    override fun getProducts(): Flow<List<Product>> {
        return productsDao.getProducts()
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override suspend fun updateProducts(newList: List<Product>) {
        productsDao.insertAll(newList.map { it.toEntity() })
    }
}
