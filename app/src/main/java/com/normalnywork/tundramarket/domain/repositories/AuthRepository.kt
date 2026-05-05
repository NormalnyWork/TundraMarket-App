package com.normalnywork.tundramarket.domain.repositories

interface AuthRepository {

    suspend fun authorize(
        phoneNumber: String,
        tradingStationId: Int?,
    )
}
