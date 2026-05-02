package com.normalnywork.tundramarket.ui.screens.auth

import com.arkivanov.decompose.ComponentContext

class RoleSelectionComponent(
    componentContext: ComponentContext,
    private val onNomadSelected: () -> Unit,
    private val onTradingStationSelected: () -> Unit,
) : ComponentContext by componentContext {

    fun onNomadSelected() {
        onNomadSelected.invoke()
    }

    fun onTradingStationSelected() {
        onTradingStationSelected.invoke()
    }
}
