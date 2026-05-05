package com.normalnywork.tundramarket.data.local.source

import com.normalnywork.tundramarket.data.local.db.dao.TradingStationsDao
import com.normalnywork.tundramarket.data.local.db.mappers.toDomain
import com.normalnywork.tundramarket.data.local.db.mappers.toEntity
import com.normalnywork.tundramarket.domain.entities.TradingStation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

@Singleton
class RoomLocalTradingStationsDataStore(
    private val tradingStationsDao: TradingStationsDao,
) : LocalTradingStationsDataSource {

    override fun getTradingStations(): Flow<List<TradingStation>> {
        return tradingStationsDao.getTradingStations()
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override suspend fun updateTradingStations(newList: List<TradingStation>) {
        tradingStationsDao.insertAll(newList.map { it.toEntity() })
    }
}