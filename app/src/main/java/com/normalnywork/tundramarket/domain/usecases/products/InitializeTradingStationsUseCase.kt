package com.normalnywork.tundramarket.domain.usecases.products

import com.normalnywork.tundramarket.domain.repositories.ConfigRepository

class InitializeTradingStationsUseCase(private val repository: ConfigRepository) {

    suspend operator fun invoke() = repository.updateTradingStations()
}