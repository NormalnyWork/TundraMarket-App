package com.normalnywork.tundramarket.ui.screens.tradingstation

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.TradingStationOrdersPage
import com.normalnywork.tundramarket.domain.usecases.orders.GetNewOrdersUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.GetProcessingOrdersUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.GetTradingStationHistoryOrdersUseCase
import com.normalnywork.tundramarket.ui.tools.BaseStateHolder
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Singleton

class TradingStationOrdersPageComponent(
    componentContext: ComponentContext,
    val page: TradingStationOrdersPage,
    private val onOpenOrderDetails: (Order) -> Unit,
    getProcessingOrdersUseCase: GetProcessingOrdersUseCase,
    getNewOrdersUseCase: GetNewOrdersUseCase,
    getTradingStationHistoryOrdersUseCase: GetTradingStationHistoryOrdersUseCase,
) : ComponentContext by componentContext {

    private val stateHolder = instanceKeeper.getOrCreate { StateHolder() }

    val orders: Flow<PagingData<Order>> = when (page) {
        TradingStationOrdersPage.Active -> getProcessingOrdersUseCase()
        TradingStationOrdersPage.New -> getNewOrdersUseCase()
        TradingStationOrdersPage.History -> getTradingStationHistoryOrdersUseCase()
    }.cachedIn(stateHolder.scope)

    fun onOpenOrderDetailsClicked(order: Order) {
        onOpenOrderDetails.invoke(order)
    }

    private class StateHolder : BaseStateHolder()

    @Singleton
    class Factory(
        private val getProcessingOrdersUseCase: GetProcessingOrdersUseCase,
        private val getNewOrdersUseCase: GetNewOrdersUseCase,
        private val getTradingStationHistoryOrdersUseCase: GetTradingStationHistoryOrdersUseCase,
    ) {

        operator fun invoke(
            componentContext: ComponentContext,
            page: TradingStationOrdersPage,
            onOpenOrderDetails: (Order) -> Unit,
        ) = TradingStationOrdersPageComponent(
            componentContext = componentContext,
            page = page,
            onOpenOrderDetails = onOpenOrderDetails,
            getProcessingOrdersUseCase = getProcessingOrdersUseCase,
            getNewOrdersUseCase = getNewOrdersUseCase,
            getTradingStationHistoryOrdersUseCase = getTradingStationHistoryOrdersUseCase,
        )
    }
}
