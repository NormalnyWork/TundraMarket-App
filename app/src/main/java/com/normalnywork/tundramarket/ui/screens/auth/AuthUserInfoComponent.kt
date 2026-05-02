package com.normalnywork.tundramarket.ui.screens.auth

import com.arkivanov.decompose.ComponentContext
import com.normalnywork.tundramarket.domain.entities.UserRole

class AuthUserInfoComponent(
    componentContext: ComponentContext,
    private val role: UserRole,
    private val onBack: () -> Unit,
    private val onAuthorized: () -> Unit,
) : ComponentContext by componentContext {

    fun onBackClicked() {
        onBack.invoke()
    }

    fun onAuthorizeClicked() {
        onAuthorized.invoke()
    }

    fun getRoleLabel(): String =
        when (role) {
            UserRole.Nomad -> "Nomad"
            UserRole.TradingStation -> "Trading station"
        }
}
