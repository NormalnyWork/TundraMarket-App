package com.normalnywork.tundramarket.ui.screens.auth

import androidx.compose.foundation.text.input.TextFieldState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.normalnywork.tundramarket.domain.entities.Location
import com.normalnywork.tundramarket.domain.entities.TradingStation
import com.normalnywork.tundramarket.domain.entities.UserRole
import com.normalnywork.tundramarket.domain.usecases.config.GetTradingStationsUseCase
import com.normalnywork.tundramarket.ui.navigation.auth.AuthUserInfoComponent
import com.normalnywork.tundramarket.ui.tools.BaseStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.Singleton

class ActualAuthUserInfoComponent(
    componentContext: ComponentContext,
    override val role: UserRole,
    private val onBack: () -> Unit,
    private val onAuthorized: (phoneNumber: String, tradingStation: TradingStation?) -> Unit,
    getTradingStationsUseCase: GetTradingStationsUseCase,
) : AuthUserInfoComponent, ComponentContext by componentContext {

    private val stateHolder = instanceKeeper.getOrCreate { StateHolder() }

    override val phoneNumber = stateHolder.phoneNumber

    override val tradingStations = getTradingStationsUseCase()
        .stateIn(stateHolder.scope, started = SharingStarted.Lazily, initialValue = emptyList())

    override val selectedStation = MutableStateFlow<TradingStation?>(null)

    override fun selectTradingStation(newStation: TradingStation) {
        selectedStation.value = newStation
    }

    override fun authorize() {
        if (role == UserRole.Nomad || selectedStation.value != null) {
            onAuthorized.invoke(phoneNumber.text.toString(), selectedStation.value)
        }
    }

    override fun goBack() {
        onBack.invoke()
    }

    @Singleton
    class Factory(
        private val getTradingStationsUseCase: GetTradingStationsUseCase,
    ) : AuthUserInfoComponent.Factory {

        override fun invoke(
            componentContext: ComponentContext,
            role: UserRole,
            authorize: (phoneNumber: String, tradingStation: TradingStation?) -> Unit,
            goBack: () -> Unit,
        ) = ActualAuthUserInfoComponent(
            componentContext = componentContext,
            role = role,
            onBack = goBack,
            onAuthorized = authorize,
            getTradingStationsUseCase = getTradingStationsUseCase,
        )
    }

    class StateHolder : BaseStateHolder() {

        val phoneNumber = TextFieldState()
    }
}

class MockAuthUserInfoComponent(
    override val role: UserRole,
) : AuthUserInfoComponent {

    override val phoneNumber = TextFieldState()

    override val tradingStations = MutableStateFlow(
        listOf(
            TradingStation(
                id = 1,
                name = "Степина",
                phone = null,
                location = Location(0f, 0f),
            ),
            TradingStation(
                id = 2,
                name = "Паюта",
                phone = null,
                location = Location(0f, 0f),
            ),
        )
    )
    override val selectedStation = MutableStateFlow(
        TradingStation(
            id = 1,
            name = "Степина",
            phone = null,
            location = Location(0f, 0f),
        ),
    )

    override fun selectTradingStation(newStation: TradingStation) {}

    override fun authorize() {}

    override fun goBack() {}
}
