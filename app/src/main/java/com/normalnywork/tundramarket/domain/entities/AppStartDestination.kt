package com.normalnywork.tundramarket.domain.entities

sealed interface AppStartDestination {

    data object Auth : AppStartDestination

    data object Nomad : AppStartDestination

    data object TradingStation : AppStartDestination
}
