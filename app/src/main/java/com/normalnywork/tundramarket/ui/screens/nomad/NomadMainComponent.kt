package com.normalnywork.tundramarket.ui.screens.nomad

import com.arkivanov.decompose.ComponentContext

class NomadMainComponent(
    componentContext: ComponentContext,
    private val onOpenHistory: () -> Unit,
    private val onCreateOrder: () -> Unit,
) : ComponentContext by componentContext {

    fun onOpenHistoryClicked() {
        onOpenHistory.invoke()
    }

    fun onCreateOrderClicked() {
        onCreateOrder.invoke()
    }
}
