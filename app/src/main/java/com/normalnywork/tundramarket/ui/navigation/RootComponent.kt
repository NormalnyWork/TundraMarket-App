package com.normalnywork.tundramarket.ui.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.normalnywork.tundramarket.domain.entities.AppStartDestination
import com.normalnywork.tundramarket.ui.navigation.auth.AuthFlowComponent
import com.normalnywork.tundramarket.ui.navigation.nomad.NomadFlowComponent
import com.normalnywork.tundramarket.ui.navigation.tradingstation.TradingStationFlowComponent
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Singleton

class RootComponent(
    componentContext: ComponentContext,
    startDestination: AppStartDestination,
    private val authFlowComponentFactory: AuthFlowComponent.Factory,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val childStack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = startDestination.toConfig(),
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
                component = NomadFlowComponent(
                    componentContext = componentContext,
                ),
            )

            Config.TradingStation -> Child.TradingStation(
                component = TradingStationFlowComponent(
                    componentContext = componentContext,
                ),
            )
        }

    private fun AppStartDestination.toConfig() = when (this) {
        AppStartDestination.Auth -> Config.Auth
        AppStartDestination.Nomad -> Config.Nomad
        AppStartDestination.TradingStation -> Config.TradingStation
    }

    @Singleton
    class Factory(private val authFlowComponentFactory: AuthFlowComponent.Factory) {

        operator fun invoke(
            componentContext: ComponentContext,
            startDestination: AppStartDestination,
        ) = RootComponent(
            componentContext = componentContext,
            startDestination = startDestination,
            authFlowComponentFactory = authFlowComponentFactory,
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
