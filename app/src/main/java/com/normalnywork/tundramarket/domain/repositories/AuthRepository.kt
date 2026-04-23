package com.normalnywork.tundramarket.domain.repositories

import com.normalnywork.tundramarket.domain.entities.TradingStation

interface AuthRepository {

    suspend fun authorize(phoneNumber: String, tradingStation: TradingStation?)
}