package com.normalnywork.tundramarket.ui.screens.nomad

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.normalnywork.tundramarket.domain.entities.OrderNetworkStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.usecases.orders.ChangeOrderStatusUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.CreateOrderUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.GetCurrentOrderUseCase
import com.normalnywork.tundramarket.ui.tools.BaseStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Singleton
import com.normalnywork.tundramarket.domain.entities.Order as DomainOrder

class NomadMainComponent(
    componentContext: ComponentContext,
    private val onOpenHistory: () -> Unit,
    private val onCreateOrder: () -> Unit,
    getCurrentOrderUseCase: GetCurrentOrderUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val changeOrderStatusUseCase: ChangeOrderStatusUseCase,
) : ComponentContext by componentContext {

    private val stateHolder = instanceKeeper.getOrCreate { StateHolder() }

    val currentOrderState: StateFlow<CurrentOrderState> = getCurrentOrderUseCase()
        .map { order -> order.toCurrentOrderState() }
        .stateIn(
            scope = stateHolder.scope,
            started = SharingStarted.Lazily,
            initialValue = CurrentOrderState.Empty,
        )

    fun onOpenHistoryClicked() {
        onOpenHistory.invoke()
    }

    fun onCreateOrderClicked() {
        onCreateOrder.invoke()
    }

    fun onRepeatOrderClicked(order: CurrentOrderState.Order) {
        if (stateHolder.isRepeatingOrder.value) return

        stateHolder.isRepeatingOrder.value = true
        stateHolder.scope.launch {
            try {
                createOrderUseCase(
                    tradingStation = order.sourceOrder.tradingStation,
                    cart = order.sourceOrder.cart,
                    location = order.sourceOrder.location,
                    comment = order.sourceOrder.comment,
                )
            } finally {
                stateHolder.isRepeatingOrder.value = false
            }
        }
    }

    fun onCancelOrderClicked(order: CurrentOrderState.Order) {
        if (order.sourceOrder.status != OrderStatus.Created || stateHolder.isChangingOrderStatus.value) return

        stateHolder.isChangingOrderStatus.value = true
        stateHolder.scope.launch {
            try {
                changeOrderStatusUseCase(
                    order = order.sourceOrder,
                    status = OrderStatus.Cancelled,
                )
            } finally {
                stateHolder.isChangingOrderStatus.value = false
            }
        }
    }

    fun onCreateOrderViaSmsClicked(order: CurrentOrderState.Order) {
        // SMS transport is handled outside this screen; keep this action explicit for the UI state.
    }

    sealed interface CurrentOrderState {
        data object Empty : CurrentOrderState

        data class Order(
            val id: Int,
            val sourceOrder: DomainOrder,
            val products: List<ProductItem>,
            val networkState: OrderNetworkStatus?,
        ) : CurrentOrderState
    }

    data class ProductItem(
        val name: String,
        val quantity: Int,
    )

    private class StateHolder : BaseStateHolder() {

        val isRepeatingOrder = MutableStateFlow(false)
        val isChangingOrderStatus = MutableStateFlow(false)
    }

    private fun DomainOrder?.toCurrentOrderState(): CurrentOrderState {
        return this?.let { order ->
            CurrentOrderState.Order(
                id = order.id,
                sourceOrder = order,
                products = order.cart.map { (product, quantity) ->
                    ProductItem(
                        name = product.name,
                        quantity = quantity,
                    )
                },
                networkState = order.networkStatus,
            )
        } ?: CurrentOrderState.Empty
    }

    @Singleton
    class Factory(
        private val getCurrentOrderUseCase: GetCurrentOrderUseCase,
        private val createOrderUseCase: CreateOrderUseCase,
        private val changeOrderStatusUseCase: ChangeOrderStatusUseCase,
    ) {

        operator fun invoke(
            componentContext: ComponentContext,
            onOpenHistory: () -> Unit,
            onCreateOrder: () -> Unit,
        ) = NomadMainComponent(
            componentContext = componentContext,
            onOpenHistory = onOpenHistory,
            onCreateOrder = onCreateOrder,
            getCurrentOrderUseCase = getCurrentOrderUseCase,
            createOrderUseCase = createOrderUseCase,
            changeOrderStatusUseCase = changeOrderStatusUseCase,
        )
    }
}
