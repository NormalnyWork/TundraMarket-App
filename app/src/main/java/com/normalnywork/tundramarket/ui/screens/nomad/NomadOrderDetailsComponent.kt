package com.normalnywork.tundramarket.ui.screens.nomad

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.isTerminal
import com.normalnywork.tundramarket.domain.usecases.orders.CreateOrderUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.GetCurrentOrderUseCase
import com.normalnywork.tundramarket.ui.tools.BaseStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Singleton

class NomadOrderDetailsComponent(
    componentContext: ComponentContext,
    val order: Order,
    getCurrentOrderUseCase: GetCurrentOrderUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val onBack: () -> Unit,
    private val onOrderRepeated: () -> Unit,
) : ComponentContext by componentContext {

    private val stateHolder = instanceKeeper.getOrCreate { StateHolder() }

    val hasOngoingOrder = getCurrentOrderUseCase().map { currentOrder ->
        currentOrder?.status?.isTerminal == false
    }.stateIn(
        scope = stateHolder.scope,
        started = SharingStarted.Lazily,
        initialValue = true,
    )

    fun onBackClicked() {
        onBack.invoke()
    }

    fun onRepeatOrderClicked() {
        if (stateHolder.isRepeatingOrder.value) return

        stateHolder.isRepeatingOrder.value = true
        stateHolder.scope.launch {
            try {
                createOrderUseCase(
                    tradingStation = order.tradingStation,
                    cart = order.cart,
                    location = order.location,
                    comment = order.comment,
                )
                onOrderRepeated.invoke()
            } finally {
                stateHolder.isRepeatingOrder.value = false
            }
        }
    }

    private class StateHolder : BaseStateHolder() {

        val isRepeatingOrder = MutableStateFlow(false)
    }

    @Singleton
    class Factory(
        private val getCurrentOrderUseCase: GetCurrentOrderUseCase,
        private val createOrderUseCase: CreateOrderUseCase,
    ) {

        operator fun invoke(
            componentContext: ComponentContext,
            order: Order,
            onBack: () -> Unit,
            onOrderRepeated: () -> Unit,
        ) = NomadOrderDetailsComponent(
            componentContext = componentContext,
            order = order,
            getCurrentOrderUseCase = getCurrentOrderUseCase,
            createOrderUseCase = createOrderUseCase,
            onBack = onBack,
            onOrderRepeated = onOrderRepeated,
        )
    }
}
