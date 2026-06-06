package com.normalnywork.tundramarket.data.local.preferences

interface OrderHistorySyncStore {

    suspend fun isHistoryFullyCached(): Boolean

    suspend fun markHistoryFullyCached()
}
