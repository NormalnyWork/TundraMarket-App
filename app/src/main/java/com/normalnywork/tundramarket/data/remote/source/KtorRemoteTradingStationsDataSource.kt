package com.normalnywork.tundramarket.data.remote.source

import com.normalnywork.tundramarket.data.remote.api.mappers.toDomain
import com.normalnywork.tundramarket.data.remote.api.proto.TradingStationsListResponse
import com.normalnywork.tundramarket.data.remote.api.schema.TradingStations
import com.normalnywork.tundramarket.domain.entities.TradingStation
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import org.koin.core.annotation.Singleton

@Singleton
class KtorRemoteTradingStationsDataSource(
    private val httpClient: HttpClient,
) : RemoteTradingStationsDataSource {

    override suspend fun getTradingStations(): List<TradingStation> {
        val response = httpClient
            .get(TradingStations.List())
            .body<TradingStationsListResponse>()

        return response.tradingStations.map { it.toDomain() }
    }
}