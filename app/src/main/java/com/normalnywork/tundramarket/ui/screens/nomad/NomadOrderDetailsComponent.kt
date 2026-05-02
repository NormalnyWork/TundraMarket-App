package com.normalnywork.tundramarket.ui.screens.nomad

import com.arkivanov.decompose.ComponentContext

class NomadOrderDetailsComponent(
    componentContext: ComponentContext,
    val orderId: Int,
    private val onBack: () -> Unit,
) : ComponentContext by componentContext {

    fun onBackClicked() {
        onBack.invoke()
    }
}
