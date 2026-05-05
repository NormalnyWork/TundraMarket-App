package com.normalnywork.tundramarket.ui.navigation.auth

import androidx.compose.foundation.text.input.TextFieldState
import com.arkivanov.decompose.ComponentContext
import com.normalnywork.tundramarket.domain.entities.TradingStation
import com.normalnywork.tundramarket.domain.entities.UserRole
import kotlinx.coroutines.flow.StateFlow

interface AuthUserInfoComponent {

    val role: UserRole

    val phoneNumber: TextFieldState

    val tradingStations: StateFlow<List<TradingStation>>
    val selectedStation: StateFlow<TradingStation?>

    fun selectTradingStation(newStation: TradingStation)

    fun authorize()

    fun goBack()

    fun interface Factory {

        operator fun invoke(
            componentContext: ComponentContext,
            role: UserRole,
            authorize: () -> Unit,
            goBack: () -> Unit,
        ): AuthUserInfoComponent
    }
}