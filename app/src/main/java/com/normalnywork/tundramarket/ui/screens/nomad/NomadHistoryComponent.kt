package com.normalnywork.tundramarket.ui.screens.nomad

import com.arkivanov.decompose.ComponentContext

class NomadHistoryComponent(
    componentContext: ComponentContext,
    private val onBack: () -> Unit,
    private val onOpenOrderDetails: (Int) -> Unit,
) : ComponentContext by componentContext {

    fun onBackClicked() {
        onBack.invoke()
    }

    fun onOpenOrderDetailsClicked() {
        onOpenOrderDetails.invoke(SAMPLE_ORDER_ID)
    }

    private companion object {
        const val SAMPLE_ORDER_ID = 1024
    }
}
