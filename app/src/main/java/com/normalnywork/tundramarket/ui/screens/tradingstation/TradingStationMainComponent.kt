package com.normalnywork.tundramarket.ui.screens.tradingstation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.normalnywork.tundramarket.domain.entities.TradingStationOrdersPage

class TradingStationMainComponent(
    componentContext: ComponentContext,
    private val onOpenOrderDetails: (Int) -> Unit,
) : ComponentContext by componentContext {

    private val navigation = PagesNavigation<TradingStationOrdersPage>()

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

    fun onPageSelected(index: Int) {
        navigation.select(index)
    }

    private fun child(
        page: TradingStationOrdersPage,
        componentContext: ComponentContext,
    ): TradingStationOrdersPageComponent =
        TradingStationOrdersPageComponent(
            componentContext = componentContext,
            page = page,
            onOpenOrderDetails = { onOpenOrderDetails(page.sampleOrderId) },
        )

    private val TradingStationOrdersPage.sampleOrderId: Int
        get() = when (this) {
            TradingStationOrdersPage.Active -> 2001
            TradingStationOrdersPage.New -> 2002
            TradingStationOrdersPage.History -> 2003
        }
}
