package com.normalnywork.tundramarket.domain.usecases.auth

import com.normalnywork.tundramarket.domain.entities.TradingStation
import com.normalnywork.tundramarket.domain.repositories.AuthRepository

class AuthorizeUserUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(
        phoneNumber: String,
        tradingStation: TradingStation?,
    ) = repository.authorize(
        phoneNumber = phoneNumber,
        tradingStation = tradingStation,
    )
}