package com.normalnywork.tundramarket.domain.repositories

import com.normalnywork.tundramarket.domain.entities.TradingStation
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {

    fun getTradingStations(): Flow<List<TradingStation>>

    suspend fun updateTradingStations()
}