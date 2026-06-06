package com.normalnywork.tundramarket.ui.screens.nomad

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.usecases.orders.GetHistoryOrdersUseCase
import com.normalnywork.tundramarket.ui.tools.BaseStateHolder
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Singleton

class NomadHistoryComponent(
    componentContext: ComponentContext,
    private val onBack: () -> Unit,
    private val onOpenOrderDetails: (Int) -> Unit,
    private val onOpenNewOrderFlow: () -> Unit,
    private val getHistoryOrdersUseCase: GetHistoryOrdersUseCase,
) : ComponentContext by componentContext {

    private val stateHolder = instanceKeeper.getOrCreate { StateHolder() }

    val historyOrders: Flow<PagingData<Order>> = getHistoryOrdersUseCase()
        .cachedIn(stateHolder.scope)

    fun onBackClicked() {
        onBack.invoke()
    }

    fun onOpenOrderDetailsClicked(order: Order) {
        onOpenOrderDetails.invoke(order.id)
    }

    fun onNewOrderClicked() {
        onOpenNewOrderFlow()
    }

    private class StateHolder : BaseStateHolder()

    @Singleton
    class Factory(
        private val getHistoryOrdersUseCase: GetHistoryOrdersUseCase,
    ) {

        operator fun invoke(
            componentContext: ComponentContext,
            onBack: () -> Unit,
            onOpenOrderDetails: (Int) -> Unit,
            onOpenNewOrderFlow: () -> Unit,
        ) = NomadHistoryComponent(
            componentContext = componentContext,
            onBack = onBack,
            onOpenOrderDetails = onOpenOrderDetails,
            onOpenNewOrderFlow = onOpenNewOrderFlow,
            getHistoryOrdersUseCase = getHistoryOrdersUseCase,
        )
    }
}
