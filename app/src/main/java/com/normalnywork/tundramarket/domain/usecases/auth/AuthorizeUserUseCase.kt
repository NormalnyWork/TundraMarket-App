package com.normalnywork.tundramarket.domain.usecases.auth

import com.normalnywork.tundramarket.domain.entities.UserRole
import com.normalnywork.tundramarket.domain.repositories.AuthRepository
import org.koin.core.annotation.Singleton

@Singleton
class AuthorizeUserUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(
        phoneNumber: String,
        tradingStationId: Int?,
    ) {
        repository.authorize(
            phoneNumber = phoneNumber,
            tradingStationId = tradingStationId,
        )
        repository.saveUserRole(
            role = if (tradingStationId == null) {
                UserRole.Nomad
            } else {
                UserRole.TradingStation
            },
        )
    }
}
