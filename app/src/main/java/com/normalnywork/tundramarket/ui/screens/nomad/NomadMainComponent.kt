package com.normalnywork.tundramarket.ui.screens.nomad

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderNetworkStatus
import com.normalnywork.tundramarket.domain.usecases.orders.GetCurrentOrderUseCase
import com.normalnywork.tundramarket.ui.tools.BaseStateHolder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.Singleton

class NomadMainComponent(
    componentContext: ComponentContext,
    private val onOpenHistory: () -> Unit,
    private val onCreateOrder: () -> Unit,
    getCurrentOrderUseCase: GetCurrentOrderUseCase,
) : ComponentContext by componentContext {

    private val stateHolder = instanceKeeper.getOrCreate { StateHolder() }

    val currentOrderState: StateFlow<CurrentOrderState> = getCurrentOrderUseCase()
        .map { order -> order.toCurrentOrderState() }
        .stateIn(
            scope = stateHolder.scope,
            started = SharingStarted.Lazily,
            initialValue = CurrentOrderState.Loading,
        )

    fun onOpenHistoryClicked() {
        onOpenHistory.invoke()
    }

    fun onCreateOrderClicked() {
        onCreateOrder.invoke()
    }

    sealed interface CurrentOrderState {
        data object Loading : CurrentOrderState
        data object Empty : CurrentOrderState
        data class Order(
            val id: Int,
            val syncState: SyncState,
        ) : CurrentOrderState
    }

    enum class SyncState {
        Enqueued,
        Processing,
        Failed,
        Created,
    }

    private class StateHolder : BaseStateHolder()

    private fun Order?.toCurrentOrderState(): CurrentOrderState {
        return this?.let { order ->
            CurrentOrderState.Order(
                id = order.id,
                syncState = order.networkStatus.toSyncState(),
            )
        } ?: CurrentOrderState.Empty
    }

    private fun OrderNetworkStatus?.toSyncState(): SyncState {
        return when (this) {
            OrderNetworkStatus.Enqueued -> SyncState.Enqueued
            OrderNetworkStatus.Processing,
            OrderNetworkStatus.Loading,
            OrderNetworkStatus.LoadingSms -> SyncState.Processing
            OrderNetworkStatus.Failed,
            OrderNetworkStatus.SmsFailed -> SyncState.Failed
            null -> SyncState.Created
        }
    }

    @Singleton
    class Factory(private val getCurrentOrderUseCase: GetCurrentOrderUseCase) {

        operator fun invoke(
            componentContext: ComponentContext,
            onOpenHistory: () -> Unit,
            onCreateOrder: () -> Unit,
        ) = NomadMainComponent(
            componentContext = componentContext,
            onOpenHistory = onOpenHistory,
            onCreateOrder = onCreateOrder,
            getCurrentOrderUseCase = getCurrentOrderUseCase,
        )
    }
}
