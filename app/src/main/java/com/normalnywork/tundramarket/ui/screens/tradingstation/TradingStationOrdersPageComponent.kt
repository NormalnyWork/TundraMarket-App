package com.normalnywork.tundramarket.ui.screens.tradingstation

import com.arkivanov.decompose.ComponentContext
import com.normalnywork.tundramarket.domain.entities.TradingStationOrdersPage

class TradingStationOrdersPageComponent(
    componentContext: ComponentContext,
    val page: TradingStationOrdersPage,
    private val onOpenOrderDetails: () -> Unit,
) : ComponentContext by componentContext {

    fun onOpenOrderDetailsClicked() {
        onOpenOrderDetails.invoke()
    }
}
