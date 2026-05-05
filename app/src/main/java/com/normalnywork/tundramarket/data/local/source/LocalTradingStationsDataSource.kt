package com.normalnywork.tundramarket.data.local.source

import com.normalnywork.tundramarket.domain.entities.TradingStation
import kotlinx.coroutines.flow.Flow

interface LocalTradingStationsDataSource {

    fun getTradingStations(): Flow<List<TradingStation>>

    suspend fun updateTradingStations(newList: List<TradingStation>)
}