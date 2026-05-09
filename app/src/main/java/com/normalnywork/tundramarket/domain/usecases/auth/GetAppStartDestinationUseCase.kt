package com.normalnywork.tundramarket.domain.usecases.auth

import com.normalnywork.tundramarket.domain.entities.AppStartDestination
import com.normalnywork.tundramarket.domain.entities.UserRole
import com.normalnywork.tundramarket.domain.repositories.AuthRepository
import org.koin.core.annotation.Singleton

@Singleton
class GetAppStartDestinationUseCase(private val repository: AuthRepository) {

    suspend operator fun invoke(): AppStartDestination {
        if (!repository.isLoggedIn()) return AppStartDestination.Auth

        return when (repository.getUserRole()) {
            UserRole.Nomad -> AppStartDestination.Nomad
            UserRole.TradingStation -> AppStartDestination.TradingStation
            null -> AppStartDestination.Auth
        }
    }
}
