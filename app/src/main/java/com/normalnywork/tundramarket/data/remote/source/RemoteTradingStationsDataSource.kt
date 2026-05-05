package com.normalnywork.tundramarket.data.remote.source

import com.normalnywork.tundramarket.domain.entities.TradingStation

interface RemoteTradingStationsDataSource {

    suspend fun getTradingStations(): List<TradingStation>
}