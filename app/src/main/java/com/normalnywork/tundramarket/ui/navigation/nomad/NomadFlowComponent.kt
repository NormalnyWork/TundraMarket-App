package com.normalnywork.tundramarket.ui.navigation.nomad

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.normalnywork.tundramarket.ui.screens.nomad.NomadCreateOrderComponent
import com.normalnywork.tundramarket.ui.screens.nomad.NomadHistoryComponent
import com.normalnywork.tundramarket.ui.screens.nomad.NomadMainComponent
import com.normalnywork.tundramarket.ui.screens.nomad.NomadOrderDetailsComponent
import kotlinx.serialization.Serializable

@OptIn(DelicateDecomposeApi::class)
class NomadFlowComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val childStack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Main,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            Config.Main -> Child.Main(
                component = NomadMainComponent(
                    componentContext = componentContext,
                    onOpenHistory = { navigation.pushNew(Config.History) },
                    onCreateOrder = { navigation.pushNew(Config.CreateOrder) },
                ),
            )

            Config.History -> Child.History(
                component = NomadHistoryComponent(
                    componentContext = componentContext,
                    onBack = navigation::pop,
                    onOpenOrderDetails = { orderId -> navigation.pushNew(Config.OrderDetails(orderId)) },
                ),
            )

            is Config.OrderDetails -> Child.OrderDetails(
                component = NomadOrderDetailsComponent(
                    componentContext = componentContext,
                    orderId = config.orderId,
                    onBack = navigation::pop,
                ),
            )

            Config.CreateOrder -> Child.CreateOrder(
                component = NomadCreateOrderComponent(
                    componentContext = componentContext,
                    onBack = navigation::pop,
                ),
            )
        }

    sealed interface Child {
        class Main(val component: NomadMainComponent) : Child
        class History(val component: NomadHistoryComponent) : Child
        class OrderDetails(val component: NomadOrderDetailsComponent) : Child
        class CreateOrder(val component: NomadCreateOrderComponent) : Child
    }

    fun onBackClicked() {
        navigation.pop()
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object Main : Config

        @Serializable
        data object History : Config

        @Serializable
        data object CreateOrder : Config

        @Serializable
        data class OrderDetails(val orderId: Int) : Config
    }
}
