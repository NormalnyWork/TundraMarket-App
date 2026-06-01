package com.normalnywork.tundramarket.ui.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.normalnywork.tundramarket.domain.entities.UserRole
import com.normalnywork.tundramarket.ui.navigation.auth.AuthFlowComponent
import com.normalnywork.tundramarket.ui.navigation.nomad.NomadFlowComponent
import com.normalnywork.tundramarket.ui.navigation.tradingstation.TradingStationFlowComponent
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Singleton

class RootComponent(
    componentContext: ComponentContext,
    userRole: UserRole?,
    private val authFlowComponentFactory: AuthFlowComponent.Factory,
    private val nomadFlowComponentFactory: NomadFlowComponent.Factory,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val childStack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = userRole.toStartConfig(),
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            Config.Auth -> Child.Auth(
                component = authFlowComponentFactory(
                    componentContext = componentContext,
                    onAuthorizedAsNomad = { navigation.replaceAll(Config.Nomad) },
                    onAuthorizedAsTradingStation = { navigation.replaceAll(Config.TradingStation) },
                ),
            )

            Config.Nomad -> Child.Nomad(
                component = nomadFlowComponentFactory(
                    componentContext = componentContext,
                ),
            )

            Config.TradingStation -> Child.TradingStation(
                component = TradingStationFlowComponent(
                    componentContext = componentContext,
                ),
            )
        }

    fun onBackClicked() {
        when (val instance = childStack.active.instance) {
            is Child.Auth -> instance.component.onBackClicked()
            is Child.Nomad -> instance.component.onBackClicked()
            is Child.TradingStation -> instance.component.onBackClicked()
        }
    }

    private fun UserRole?.toStartConfig() = when (this) {
        UserRole.Nomad -> Config.Nomad
        UserRole.TradingStation -> Config.TradingStation
        null -> Config.Auth
    }

    @Singleton
    class Factory(
        private val authFlowComponentFactory: AuthFlowComponent.Factory,
        private val nomadFlowComponentFactory: NomadFlowComponent.Factory,
    ) {

        operator fun invoke(
            componentContext: ComponentContext,
            userRole: UserRole?,
        ) = RootComponent(
            componentContext = componentContext,
            userRole = userRole,
            authFlowComponentFactory = authFlowComponentFactory,
            nomadFlowComponentFactory = nomadFlowComponentFactory,
        )
    }

    sealed interface Child {
        class Auth(val component: AuthFlowComponent) : Child
        class Nomad(val component: NomadFlowComponent) : Child
        class TradingStation(val component: TradingStationFlowComponent) : Child
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object Auth : Config

        @Serializable
        data object Nomad : Config

        @Serializable
        data object TradingStation : Config
    }
}
