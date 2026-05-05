package com.normalnywork.tundramarket.data

import com.normalnywork.tundramarket.data.local.source.LocalProductsDataSource
import com.normalnywork.tundramarket.data.remote.source.RemoteProductsDataSource
import com.normalnywork.tundramarket.domain.entities.Product
import com.normalnywork.tundramarket.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Singleton

@Singleton
class ProductRepositoryImpl(
    private val localProductsDataSource: LocalProductsDataSource,
    private val remoteProductsDataSource: RemoteProductsDataSource,
) : ProductRepository {

    override fun getCatalog(): Flow<List<Product>> {
        return localProductsDataSource.getProducts()
            .onEach { list ->
                if (list.isEmpty()) {
                    runCatching { updateCatalog() }
                        .onFailure { it.printStackTrace() }
                }
            }
    }

    override suspend fun updateCatalog() {
        val catalog = remoteProductsDataSource.getCatalog()
        localProductsDataSource.updateProducts(catalog)
    }
}
