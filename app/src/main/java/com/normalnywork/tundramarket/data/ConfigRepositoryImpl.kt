package com.normalnywork.tundramarket.data

import com.normalnywork.tundramarket.data.local.source.LocalTradingStationsDataSource
import com.normalnywork.tundramarket.data.remote.source.RemoteTradingStationsDataSource
import com.normalnywork.tundramarket.domain.entities.TradingStation
import com.normalnywork.tundramarket.domain.repositories.ConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Singleton

@Singleton
class ConfigRepositoryImpl(
    private val localTradingStationsDataSource: LocalTradingStationsDataSource,
    private val remoteTradingStationsDataSource: RemoteTradingStationsDataSource,
) : ConfigRepository {

    override fun getTradingStations(): Flow<List<TradingStation>> {
        return localTradingStationsDataSource.getTradingStations()
            .onEach { list -> if (list.isEmpty()) updateTradingStations() }
    }

    override suspend fun updateTradingStations() {
        runCatching {
            remoteTradingStationsDataSource.getTradingStations()
        }.onSuccess {
            localTradingStationsDataSource.updateTradingStations(it)
        }.onFailure {
            it.printStackTrace()
        }
    }
}