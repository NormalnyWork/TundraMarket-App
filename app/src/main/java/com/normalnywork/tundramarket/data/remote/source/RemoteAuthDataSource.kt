package com.normalnywork.tundramarket.data.remote.source

interface RemoteAuthDataSource {

    suspend fun authorize(
        phoneNumber: String,
        tradingStationId: Int?,
    ): String
}
