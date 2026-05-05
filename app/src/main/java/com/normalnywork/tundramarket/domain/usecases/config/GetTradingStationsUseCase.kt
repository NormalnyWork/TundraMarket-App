package com.normalnywork.tundramarket.domain.usecases.config

import com.normalnywork.tundramarket.domain.repositories.ConfigRepository
import org.koin.core.annotation.Singleton

@Singleton
class GetTradingStationsUseCase(private val repository: ConfigRepository) {

    operator fun invoke() = repository.getTradingStations()
}