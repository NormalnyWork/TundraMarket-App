package com.normalnywork.tundramarket.ui.screens.auth

import com.arkivanov.decompose.ComponentContext
import com.normalnywork.tundramarket.ui.navigation.auth.RoleSelectionComponent

class ActualRoleSelectionComponent(
    componentContext: ComponentContext,
    private val onNomadSelected: () -> Unit,
    private val onTradingStationSelected: () -> Unit,
) : RoleSelectionComponent, ComponentContext by componentContext {

    override fun onNomadSelected() {
        onNomadSelected.invoke()
    }

    override fun onTradingStationSelected() {
        onTradingStationSelected.invoke()
    }
}

class MockRoleSelectionComponent : RoleSelectionComponent {

    override fun onNomadSelected() {}

    override fun onTradingStationSelected() {}
}
