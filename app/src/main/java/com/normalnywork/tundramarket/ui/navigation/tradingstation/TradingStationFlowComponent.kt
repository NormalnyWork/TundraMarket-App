package com.normalnywork.tundramarket.ui.navigation.tradingstation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.ui.screens.tradingstation.TradingStationMainComponent
import com.normalnywork.tundramarket.ui.screens.tradingstation.TradingStationOrderDetailsComponent
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Singleton

@OptIn(DelicateDecomposeApi::class)
class TradingStationFlowComponent(
    componentContext: ComponentContext,
    private val tradingStationMainComponentFactory: TradingStationMainComponent.Factory,
    private val tradingStationOrderDetailsComponentFactory: TradingStationOrderDetailsComponent.Factory,
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
                component = tradingStationMainComponentFactory(
                    componentContext = componentContext,
                    onOpenOrderDetails = { order -> navigation.pushNew(Config.OrderDetails(order)) },
                ),
            )

            is Config.OrderDetails -> Child.OrderDetails(
                component = tradingStationOrderDetailsComponentFactory(
                    componentContext = componentContext,
                    order = config.order,
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
        data class OrderDetails(val order: Order) : Config
    }

    @Singleton
    class Factory(
        private val tradingStationMainComponentFactory: TradingStationMainComponent.Factory,
        private val tradingStationOrderDetailsComponentFactory: TradingStationOrderDetailsComponent.Factory,
    ) {

        operator fun invoke(componentContext: ComponentContext) = TradingStationFlowComponent(
            componentContext = componentContext,
            tradingStationMainComponentFactory = tradingStationMainComponentFactory,
            tradingStationOrderDetailsComponentFactory = tradingStationOrderDetailsComponentFactory,
        )
    }
}
