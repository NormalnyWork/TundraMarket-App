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
import com.normalnywork.tundramarket.ui.screens.auth.AuthUserInfoComponent
import com.normalnywork.tundramarket.ui.screens.auth.RoleSelectionComponent
import kotlinx.serialization.Serializable

@OptIn(DelicateDecomposeApi::class)
class AuthFlowComponent(
    componentContext: ComponentContext,
    private val onAuthorizedAsNomad: () -> Unit,
    private val onAuthorizedAsTradingStation: () -> Unit,
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
                component = RoleSelectionComponent(
                    componentContext = componentContext,
                    onNomadSelected = { navigation.pushNew(Config.UserInfo(UserRole.Nomad)) },
                    onTradingStationSelected = {
                        navigation.pushNew(Config.UserInfo(UserRole.TradingStation))
                    },
                ),
            )

            is Config.UserInfo -> Child.UserInfo(
                component = AuthUserInfoComponent(
                    componentContext = componentContext,
                    role = config.role,
                    onBack = navigation::pop,
                    onAuthorized = {
                        when (config.role) {
                            UserRole.Nomad -> onAuthorizedAsNomad()
                            UserRole.TradingStation -> onAuthorizedAsTradingStation()
                        }
                    },
                ),
            )
        }

    sealed interface Child {
        class RoleSelection(val component: RoleSelectionComponent) : Child
        class UserInfo(val component: AuthUserInfoComponent) : Child
    }

    fun onBackClicked() {
        navigation.pop()
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object RoleSelection : Config

        @Serializable
        data class UserInfo(val role: UserRole) : Config
    }
}
