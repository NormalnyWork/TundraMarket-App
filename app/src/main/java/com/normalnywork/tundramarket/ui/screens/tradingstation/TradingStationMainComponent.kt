package com.normalnywork.tundramarket.ui.screens.tradingstation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.TradingStationOrdersPage
import com.normalnywork.tundramarket.domain.usecases.orders.HasNewOrdersUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.UpdateOrdersUseCase
import com.normalnywork.tundramarket.domain.usecases.products.InitializeCatalogUseCase
import com.normalnywork.tundramarket.domain.usecases.products.InitializeTradingStationsUseCase
import com.normalnywork.tundramarket.ui.tools.BaseStateHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Singleton

class TradingStationMainComponent(
    componentContext: ComponentContext,
    private val onOpenOrderDetails: (Order) -> Unit,
    private val tradingStationOrdersPageComponentFactory: TradingStationOrdersPageComponent.Factory,
    hasNewOrdersUseCase: HasNewOrdersUseCase,
    private val updateOrdersUseCase: UpdateOrdersUseCase,
    private val initializeTradingStationsUseCase: InitializeTradingStationsUseCase,
    private val initializeCatalogUseCase: InitializeCatalogUseCase,
) : ComponentContext by componentContext {

    private val stateHolder = instanceKeeper.getOrCreate { StateHolder() }
    private val navigation = PagesNavigation<TradingStationOrdersPage>()

    val hasNewOrders: StateFlow<Boolean> = hasNewOrdersUseCase()
        .stateIn(
            scope = stateHolder.scope,
            started = SharingStarted.Lazily,
            initialValue = false,
        )

    val childPages: Value<ChildPages<TradingStationOrdersPage, TradingStationOrdersPageComponent>> = childPages(
        source = navigation,
        serializer = TradingStationOrdersPage.serializer(),
        initialPages = {
            Pages(
                items = listOf(
                    TradingStationOrdersPage.Active,
                    TradingStationOrdersPage.New,
                    TradingStationOrdersPage.History,
                ),
                selectedIndex = 0,
            )
        },
        childFactory = ::child,
    )

    init {
        stateHolder.startSyncIfNeeded(
            initialize = {
                runCatching { initializeTradingStationsUseCase() }
                    .onFailure { it.printStackTrace() }
                runCatching { initializeCatalogUseCase() }
                    .onFailure { it.printStackTrace() }
            },
            sync = { updateOrdersUseCase() },
        )
    }

    fun onPageSelected(index: Int) {
        navigation.select(index)
    }

    fun onNewOrdersClicked() {
        navigation.select(NEW_ORDERS_PAGE_INDEX)
    }

    private fun child(
        page: TradingStationOrdersPage,
        componentContext: ComponentContext,
    ): TradingStationOrdersPageComponent =
        tradingStationOrdersPageComponentFactory(
            componentContext = componentContext,
            page = page,
            onOpenOrderDetails = onOpenOrderDetails,
        )

    private class StateHolder : BaseStateHolder() {

        private var isSyncStarted = false

        fun startSyncIfNeeded(
            initialize: suspend () -> Unit,
            sync: suspend () -> Unit,
        ) {
            if (isSyncStarted) return
            isSyncStarted = true

            scope.launch {
                initialize()

                while (true) {
                    runCatching { sync() }
                        .onFailure { it.printStackTrace() }
                    delay(ORDERS_SYNC_INTERVAL_MS)
                }
            }
        }
    }

    @Singleton
    class Factory(
        private val tradingStationOrdersPageComponentFactory: TradingStationOrdersPageComponent.Factory,
        private val hasNewOrdersUseCase: HasNewOrdersUseCase,
        private val updateOrdersUseCase: UpdateOrdersUseCase,
        private val initializeTradingStationsUseCase: InitializeTradingStationsUseCase,
        private val initializeCatalogUseCase: InitializeCatalogUseCase,
    ) {

        operator fun invoke(
            componentContext: ComponentContext,
            onOpenOrderDetails: (Order) -> Unit,
        ) = TradingStationMainComponent(
            componentContext = componentContext,
            onOpenOrderDetails = onOpenOrderDetails,
            tradingStationOrdersPageComponentFactory = tradingStationOrdersPageComponentFactory,
            hasNewOrdersUseCase = hasNewOrdersUseCase,
            updateOrdersUseCase = updateOrdersUseCase,
            initializeTradingStationsUseCase = initializeTradingStationsUseCase,
            initializeCatalogUseCase = initializeCatalogUseCase,
        )
    }

    private companion object {

        const val NEW_ORDERS_PAGE_INDEX = 1
        const val ORDERS_SYNC_INTERVAL_MS = 30_000L
    }
}
