package com.normalnywork.tundramarket.data.local.preferences

import com.normalnywork.tundramarket.domain.entities.TradingStationOrdersPage

interface OrderHistorySyncStore {

    suspend fun isHistoryFullyCached(): Boolean

    suspend fun markHistoryFullyCached()

    suspend fun isTradingStationPageFullyCached(page: TradingStationOrdersPage): Boolean

    suspend fun markTradingStationPageFullyCached(page: TradingStationOrdersPage)

    suspend fun areTradingStationPagesFullyCached(): Boolean

    suspend fun getTradingStationOrdersLastUpdated(): Long

    suspend fun setTradingStationOrdersLastUpdated(lastUpdated: Long)
}
