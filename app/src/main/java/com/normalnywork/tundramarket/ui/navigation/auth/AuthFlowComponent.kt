package com.normalnywork.tundramarket.ui.navigation.auth

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.normalnywork.tundramarket.domain.entities.UserRole
import com.normalnywork.tundramarket.ui.screens.auth.ActualRoleSelectionComponent
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Singleton

@OptIn(DelicateDecomposeApi::class)
class AuthFlowComponent(
    componentContext: ComponentContext,
    private val onAuthorizedAsNomad: () -> Unit,
    private val onAuthorizedAsTradingStation: () -> Unit,
    private val authUserInfoComponentFactory: AuthUserInfoComponent.Factory,
    private val authInitializationComponentFactory: AuthInitializationComponent.Factory,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val childStack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.RoleSelection,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            Config.RoleSelection -> Child.RoleSelection(
                component = ActualRoleSelectionComponent(
                    componentContext = componentContext,
                    onNomadSelected = { navigation.pushNew(Config.UserInfo(UserRole.Nomad)) },
                    onTradingStationSelected = {
                        navigation.pushNew(Config.UserInfo(UserRole.TradingStation))
                    },
                ),
            )

            is Config.UserInfo -> Child.UserInfo(
                component = authUserInfoComponentFactory(
                    componentContext = componentContext,
                    role = config.role,
                    goBack = navigation::pop,
                    authorize = { phoneNumber, tradingStation ->
                        navigation.pushNew(
                            Config.Initialization(
                                role = config.role,
                                phoneNumber = phoneNumber,
                                tradingStationId = tradingStation?.id,
                            ),
                        )
                    },
                ),
            )

            is Config.Initialization -> Child.Initialization(
                component = authInitializationComponentFactory(
                    componentContext = componentContext,
                    role = config.role,
                    phoneNumber = config.phoneNumber,
                    tradingStationId = config.tradingStationId,
                    proceed = {
                        when (config.role) {
                            UserRole.Nomad -> onAuthorizedAsNomad()
                            UserRole.TradingStation -> onAuthorizedAsTradingStation()
                        }
                    },
                ),
            )
        }

    fun onBackClicked() {
        navigation.pop()
    }

    @Singleton
    class Factory(
        private val authUserInfoComponentFactory: AuthUserInfoComponent.Factory,
        private val authInitializationComponentFactory: AuthInitializationComponent.Factory,
    ) {

        operator fun invoke(
            componentContext: ComponentContext,
            onAuthorizedAsNomad: () -> Unit,
            onAuthorizedAsTradingStation: () -> Unit,
        ) = AuthFlowComponent(
            componentContext = componentContext,
            onAuthorizedAsNomad = onAuthorizedAsNomad,
            onAuthorizedAsTradingStation = onAuthorizedAsTradingStation,
            authUserInfoComponentFactory = authUserInfoComponentFactory,
            authInitializationComponentFactory = authInitializationComponentFactory,
        )
    }

    sealed interface Child {
        class RoleSelection(val component: RoleSelectionComponent) : Child
        class UserInfo(val component: AuthUserInfoComponent) : Child
        class Initialization(val component: AuthInitializationComponent) : Child
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object RoleSelection : Config

        @Serializable
        data class UserInfo(val role: UserRole) : Config

        @Serializable
        data class Initialization(
            val role: UserRole,
            val phoneNumber: String,
            val tradingStationId: Int?,
        ) : Config
    }
}
