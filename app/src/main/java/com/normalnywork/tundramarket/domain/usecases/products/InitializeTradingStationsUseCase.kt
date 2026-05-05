package com.normalnywork.tundramarket.domain.usecases.products

import com.normalnywork.tundramarket.domain.repositories.ConfigRepository
import org.koin.core.annotation.Singleton

@Singleton
class InitializeTradingStationsUseCase(private val repository: ConfigRepository) {

    suspend operator fun invoke() = repository.updateTradingStations()
}
