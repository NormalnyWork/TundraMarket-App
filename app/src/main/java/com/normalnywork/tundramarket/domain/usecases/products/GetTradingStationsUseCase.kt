package com.normalnywork.tundramarket.domain.usecases.products

import com.normalnywork.tundramarket.domain.repositories.ConfigRepository

class GetTradingStationsUseCase(private val repository: ConfigRepository) {

    operator fun invoke() = repository.getTradingStations()
}