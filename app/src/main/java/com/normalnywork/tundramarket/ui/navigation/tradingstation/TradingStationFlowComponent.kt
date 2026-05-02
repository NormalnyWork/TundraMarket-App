package com.normalnywork.tundramarket.ui.navigation.tradingstation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.normalnywork.tundramarket.ui.screens.tradingstation.TradingStationMainComponent
import com.normalnywork.tundramarket.ui.screens.tradingstation.TradingStationOrderDetailsComponent
import kotlinx.serialization.Serializable

@OptIn(DelicateDecomposeApi::class)
class TradingStationFlowComponent(
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
                component = TradingStationMainComponent(
                    componentContext = componentContext,
                    onOpenOrderDetails = { orderId -> navigation.pushNew(Config.OrderDetails(orderId)) },
                ),
            )

            is Config.OrderDetails -> Child.OrderDetails(
                component = TradingStationOrderDetailsComponent(
                    componentContext = componentContext,
                    orderId = config.orderId,
                    onBack = navigation::pop,
                ),
            )
        }

    sealed interface Child {
        class Main(val component: TradingStationMainComponent) : Child
        class OrderDetails(val component: TradingStationOrderDetailsComponent) : Child
    }

    fun onBackClicked() {
        navigation.pop()
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object Main : Config

        @Serializable
        data class OrderDetails(val orderId: Int) : Config
    }
}
