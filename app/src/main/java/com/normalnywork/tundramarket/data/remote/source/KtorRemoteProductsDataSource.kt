package com.normalnywork.tundramarket.data.remote.source

import com.normalnywork.tundramarket.data.remote.api.mappers.toDomain
import com.normalnywork.tundramarket.data.remote.api.proto.CatalogResponse
import com.normalnywork.tundramarket.data.remote.api.schema.User
import com.normalnywork.tundramarket.domain.entities.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import org.koin.core.annotation.Singleton

@Singleton
class KtorRemoteProductsDataSource(
    private val httpClient: HttpClient,
) : RemoteProductsDataSource {

    override suspend fun getCatalog(): List<Product> {
        val response = httpClient
            .get(User.Catalog())
            .body<CatalogResponse>()

        return response.products.map { it.toDomain() }
    }
}
