package com.normalnywork.tundramarket.ui.screens.nomad

import com.arkivanov.decompose.ComponentContext

class NomadCreateOrderComponent(
    componentContext: ComponentContext,
    private val onBack: () -> Unit,
) : ComponentContext by componentContext {

    fun onBackClicked() {
        onBack.invoke()
    }
}
