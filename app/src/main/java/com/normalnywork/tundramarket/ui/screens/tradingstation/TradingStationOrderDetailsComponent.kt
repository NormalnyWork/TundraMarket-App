package com.normalnywork.tundramarket.ui.screens.tradingstation

import com.arkivanov.decompose.ComponentContext
import com.normalnywork.tundramarket.domain.entities.Order
import org.koin.core.annotation.Singleton

class TradingStationOrderDetailsComponent(
    componentContext: ComponentContext,
    val order: Order,
    private val onBack: () -> Unit,
) : ComponentContext by componentContext {

    fun onBackClicked() {
        onBack.invoke()
    }

    @Singleton
    class Factory {

        operator fun invoke(
            componentContext: ComponentContext,
            order: Order,
            onBack: () -> Unit,
        ) = TradingStationOrderDetailsComponent(
            componentContext = componentContext,
            order = order,
            onBack = onBack,
        )
    }
}
