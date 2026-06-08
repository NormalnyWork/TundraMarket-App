package com.normalnywork.tundramarket.ui.screens.tradingstation

import androidx.compose.foundation.text.input.TextFieldState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.usecases.orders.ChangeOrderStatusUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.GetOrderUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.SetOrderProductAssembledUseCase
import com.normalnywork.tundramarket.ui.tools.BaseStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Singleton

class TradingStationOrderDetailsComponent(
    componentContext: ComponentContext,
    initialOrder: Order,
    private val onBack: () -> Unit,
    getOrderUseCase: GetOrderUseCase,
    private val changeOrderStatusUseCase: ChangeOrderStatusUseCase,
    private val setOrderProductAssembledUseCase: SetOrderProductAssembledUseCase,
) : ComponentContext by componentContext {

    private val stateHolder = instanceKeeper.getOrCreate { StateHolder() }

    val order: StateFlow<Order> = getOrderUseCase(initialOrder.id)
        .map { order -> order ?: initialOrder }
        .stateIn(
            scope = stateHolder.scope,
            started = SharingStarted.Lazily,
            initialValue = initialOrder,
        )

    val declineComment = stateHolder.declineComment

    val isDeclineDialogVisible = stateHolder.isDeclineDialogVisible

    val isIncompleteSendDialogVisible = stateHolder.isIncompleteSendDialogVisible

    fun onBackClicked() {
        onBack.invoke()
    }

    fun onAcceptOrderClicked() {
        changeStatus(status = OrderStatus.Processing)
    }

    fun onDeclineOrderClicked() {
        stateHolder.isDeclineDialogVisible.value = true
    }

    fun onDeclineDialogDismissed() {
        stateHolder.isDeclineDialogVisible.value = false
    }

    fun onDeclineOrderConfirmed() {
        val comment = declineComment.text.toString().trim()
        if (comment.isBlank()) return

        stateHolder.isDeclineDialogVisible.value = false
        changeStatus(
            status = OrderStatus.Denied,
            comment = comment,
            onSuccess = onBack,
        )
    }

    fun onOrderProductAssembledChanged(
        productId: Int,
        isAssembled: Boolean,
    ) {
        val currentOrder = order.value

        stateHolder.scope.launch {
            setOrderProductAssembledUseCase(
                order = currentOrder,
                productId = productId,
                isAssembled = isAssembled,
            )
        }
    }

    fun onSendOrderClicked() {
        val currentOrder = order.value

        if (currentOrder.assembledProductIds.size < currentOrder.cart.size) {
            stateHolder.isIncompleteSendDialogVisible.value = true
        } else {
            changeStatus(status = OrderStatus.Sent)
        }
    }

    fun onIncompleteSendDialogDismissed() {
        stateHolder.isIncompleteSendDialogVisible.value = false
    }

    fun onIncompleteSendConfirmed() {
        stateHolder.isIncompleteSendDialogVisible.value = false
        changeStatus(status = OrderStatus.Sent)
    }

    fun onDeliveryConfirmed() {
        changeStatus(status = OrderStatus.Completed)
    }

    private fun changeStatus(
        status: OrderStatus,
        comment: String? = null,
        onSuccess: () -> Unit = {},
    ) {
        if (stateHolder.isChangingStatus.value) return

        stateHolder.isChangingStatus.value = true
        stateHolder.scope.launch {
            try {
                changeOrderStatusUseCase(
                    order = order.value,
                    status = status,
                    comment = comment,
                )
                onSuccess()
            } finally {
                stateHolder.isChangingStatus.value = false
            }
        }
    }

    private class StateHolder : BaseStateHolder() {

        val declineComment = TextFieldState()
        val isDeclineDialogVisible = MutableStateFlow(false)
        val isIncompleteSendDialogVisible = MutableStateFlow(false)
        val isChangingStatus = MutableStateFlow(false)
    }

    @Singleton
    class Factory(
        private val getOrderUseCase: GetOrderUseCase,
        private val changeOrderStatusUseCase: ChangeOrderStatusUseCase,
        private val setOrderProductAssembledUseCase: SetOrderProductAssembledUseCase,
    ) {

        operator fun invoke(
            componentContext: ComponentContext,
            order: Order,
            onBack: () -> Unit,
        ) = TradingStationOrderDetailsComponent(
            componentContext = componentContext,
            initialOrder = order,
            onBack = onBack,
            getOrderUseCase = getOrderUseCase,
            changeOrderStatusUseCase = changeOrderStatusUseCase,
            setOrderProductAssembledUseCase = setOrderProductAssembledUseCase,
        )
    }
}
