package com.normalnywork.tundramarket.ui.screens.tradingstation

import com.arkivanov.decompose.ComponentContext

class TradingStationOrderDetailsComponent(
    componentContext: ComponentContext,
    val orderId: Int,
    private val onBack: () -> Unit,
) : ComponentContext by componentContext {

    fun onBackClicked() {
        onBack.invoke()
    }
}
