package com.normalnywork.tundramarket.ui.screens.nomad

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.normalnywork.tundramarket.data.local.preferences.SmsPermissionStore
import com.normalnywork.tundramarket.domain.entities.OrderNetworkStatus
import com.normalnywork.tundramarket.domain.entities.OrderSmsCommentState
import com.normalnywork.tundramarket.domain.entities.OrderSmsSendState
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.usecases.orders.ChangeOrderStatusUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.CreateOrderUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.GetCurrentOrderUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.GetOrderSmsCommentStateUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.GetOrderSmsSendStateUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.SendOrderViaSmsUseCase
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
    private val sendOrderViaSmsUseCase: SendOrderViaSmsUseCase,
    private val getOrderSmsSendStateUseCase: GetOrderSmsSendStateUseCase,
    private val getOrderSmsCommentStateUseCase: GetOrderSmsCommentStateUseCase,
    private val smsPermissionStore: SmsPermissionStore,
) : ComponentContext by componentContext {

    private val stateHolder = instanceKeeper.getOrCreate { StateHolder() }

    val isCreateOrderViaSmsForbidden: StateFlow<Boolean> = smsPermissionStore
        .isCreateOrderViaSmsForbidden()
        .stateIn(
            scope = stateHolder.scope,
            started = SharingStarted.Lazily,
            initialValue = false,
        )

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

    fun onRepeatOrderClicked() {
        if (stateHolder.isRepeatingOrder.value) return

        val order = currentOrderState.value as CurrentOrderState.Order

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

    fun onCancelOrderClicked() {
        val order = currentOrderState.value as CurrentOrderState.Order

        if (
            order.sourceOrder.status != OrderStatus.Created ||
            order.sourceOrder.isCancellationForbidden ||
            stateHolder.isChangingOrderStatus.value
        ) return

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

    fun onCreateOrderViaSmsClicked(comment: String) {
        val order = currentOrderState.value as CurrentOrderState.Order

        if (
            isCreateOrderViaSmsForbidden.value ||
            stateHolder.isSendingOrderViaSms.value ||
            order.sourceOrder.tradingStation.phone == null
        ) return

        if (getOrderSmsCommentStateUseCase(order.sourceOrder, comment).canSend.not()) return

        stateHolder.isSendingOrderViaSms.value = true
        stateHolder.scope.launch {
            try {
                sendOrderViaSmsUseCase(
                    order = order.sourceOrder,
                    comment = comment,
                )
            } finally {
                stateHolder.isSendingOrderViaSms.value = false
            }
        }
    }

    fun getCreateOrderViaSmsState(): CreateOrderViaSmsState {
        val order = currentOrderState.value as? CurrentOrderState.Order
            ?: return CreateOrderViaSmsState.Unavailable
        return when (val state = getOrderSmsSendStateUseCase(order.sourceOrder)) {
            is OrderSmsSendState.Ready -> CreateOrderViaSmsState.Ready(state.comment)
            is OrderSmsSendState.CommentEditRequired -> CreateOrderViaSmsState.CommentEditRequired(state.comment)
            OrderSmsSendState.Unavailable -> CreateOrderViaSmsState.Unavailable
        }
    }

    fun getCreateOrderViaSmsCommentState(comment: String): CreateOrderViaSmsCommentState {
        val order = currentOrderState.value as? CurrentOrderState.Order
        val state = order?.let {
            getOrderSmsCommentStateUseCase(
                order = it.sourceOrder,
                comment = comment,
            )
        } ?: OrderSmsCommentState(
            smsLength = null,
            smsLimit = 0,
            canSend = false,
        )

        return CreateOrderViaSmsCommentState(
            smsLength = state.smsLength,
            smsLimit = state.smsLimit,
            canSend = state.canSend,
        )
    }

    fun onCreateOrderViaSmsForbidden() {
        stateHolder.scope.launch {
            smsPermissionStore.forbidCreateOrderViaSms()
        }
    }

    sealed interface CurrentOrderState {
        data object Empty : CurrentOrderState

        data class Order(
            val displayId: Int,
            val sourceOrder: DomainOrder,
            val products: List<ProductItem>,
            val networkState: OrderNetworkStatus?,
        ) : CurrentOrderState
    }

    sealed interface CreateOrderViaSmsState {

        data class Ready(val comment: String) : CreateOrderViaSmsState

        data class CommentEditRequired(val comment: String) : CreateOrderViaSmsState

        data object Unavailable : CreateOrderViaSmsState
    }

    data class CreateOrderViaSmsCommentState(
        val smsLength: Int?,
        val smsLimit: Int,
        val canSend: Boolean,
    )

    data class ProductItem(
        val name: String,
        val quantity: Int,
    )

    private class StateHolder : BaseStateHolder() {

        val isRepeatingOrder = MutableStateFlow(false)
        val isChangingOrderStatus = MutableStateFlow(false)
        val isSendingOrderViaSms = MutableStateFlow(false)
    }

    private fun DomainOrder?.toCurrentOrderState(): CurrentOrderState {
        return this?.let { order ->
            CurrentOrderState.Order(
                displayId = order.displayId,
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

    private val DomainOrder.isCancellationForbidden: Boolean
        get() = isCreatedViaSms || networkStatus == OrderNetworkStatus.LoadingSms

    @Singleton
    class Factory(
        private val getCurrentOrderUseCase: GetCurrentOrderUseCase,
        private val createOrderUseCase: CreateOrderUseCase,
        private val changeOrderStatusUseCase: ChangeOrderStatusUseCase,
        private val sendOrderViaSmsUseCase: SendOrderViaSmsUseCase,
        private val getOrderSmsSendStateUseCase: GetOrderSmsSendStateUseCase,
        private val getOrderSmsCommentStateUseCase: GetOrderSmsCommentStateUseCase,
        private val smsPermissionStore: SmsPermissionStore,
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
            sendOrderViaSmsUseCase = sendOrderViaSmsUseCase,
            getOrderSmsSendStateUseCase = getOrderSmsSendStateUseCase,
            getOrderSmsCommentStateUseCase = getOrderSmsCommentStateUseCase,
            smsPermissionStore = smsPermissionStore,
        )
    }
}
