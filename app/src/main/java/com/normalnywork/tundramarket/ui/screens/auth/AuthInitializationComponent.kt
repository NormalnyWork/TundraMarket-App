package com.normalnywork.tundramarket.ui.screens.auth

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.normalnywork.tundramarket.domain.entities.UserRole
import com.normalnywork.tundramarket.domain.usecases.auth.AuthorizeUserUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.InitializeCurrentOrderUseCase
import com.normalnywork.tundramarket.domain.usecases.products.InitializeCatalogUseCase
import com.normalnywork.tundramarket.domain.usecases.products.InitializeTradingStationsUseCase
import com.normalnywork.tundramarket.ui.navigation.auth.AuthInitializationComponent
import com.normalnywork.tundramarket.ui.navigation.auth.AuthInitializationComponent.Status
import com.normalnywork.tundramarket.ui.tools.BaseStateHolder
import com.normalnywork.tundramarket.utils.NetworkStatusObserver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Singleton

class ActualAuthInitializationComponent(
    componentContext: ComponentContext,
    private val role: UserRole,
    private val phoneNumber: String,
    private val tradingStationId: Int?,
    private val proceed: () -> Unit,
    private val authorizeUserUseCase: AuthorizeUserUseCase,
    private val initializeTradingStationsUseCase: InitializeTradingStationsUseCase,
    private val initializeCatalogUseCase: InitializeCatalogUseCase,
    private val initializeCurrentOrderUseCase: InitializeCurrentOrderUseCase,
    networkStatusObserver: NetworkStatusObserver,
) : AuthInitializationComponent, ComponentContext by componentContext {

    private val stateHolder = instanceKeeper.getOrCreate {
        StateHolder(
            role = role,
            networkStatusObserver = networkStatusObserver,
        )
    }

    init {
        stateHolder.scope.launch {
            runInitialization()
        }
    }

    override val auth = stateHolder.auth
    override val tradingStations = stateHolder.tradingStations
    override val catalog = stateHolder.catalog
    override val currentOrder = stateHolder.currentOrder

    private suspend fun runInitialization() {
        runStep(
            getStatus = { auth.value },
            setStatus = { auth.value = it },
        ) {
            authorizeUserUseCase(
                phoneNumber = phoneNumber,
                tradingStationId = tradingStationId,
            )
        }

        if (role == UserRole.Nomad) {
            runStep(
                getStatus = { tradingStations.value },
                setStatus = { tradingStations.value = it },
            ) {
                initializeTradingStationsUseCase()
            }

            runStep(
                getStatus = { catalog.value },
                setStatus = { catalog.value = it },
            ) {
                initializeCatalogUseCase()
            }

            runStep(
                getStatus = { currentOrder.value },
                setStatus = { currentOrder.value = it },
            ) {
                initializeCurrentOrderUseCase()
            }
        }

        proceed()
    }

    private suspend fun runStep(
        getStatus: () -> Status?,
        setStatus: (Status) -> Unit,
        action: suspend () -> Unit,
    ) {
        if (getStatus() == Status.Done) return
        setStatus(Status.Processing)

        while (getStatus() != Status.Done) {
            waitForConnection()

            runCatching { action() }
                .onSuccess {
                    setStatus(Status.Done)
                }
                .onFailure { error ->
                    error.printStackTrace()
                    waitForRetry()
                }
        }
    }

    private suspend fun waitForConnection() {
        if (!stateHolder.isOnline.value) {
            stateHolder.isOnline.filter { it }.first()
        }
    }

    private suspend fun waitForRetry() {
        if (stateHolder.isOnline.value) {
            delay(RETRY_DELAY_MS)
        } else {
            waitForConnection()
        }
    }

    @Singleton
    class Factory(
        private val authorizeUserUseCase: AuthorizeUserUseCase,
        private val initializeTradingStationsUseCase: InitializeTradingStationsUseCase,
        private val initializeCatalogUseCase: InitializeCatalogUseCase,
        private val initializeCurrentOrderUseCase: InitializeCurrentOrderUseCase,
        private val networkStatusObserver: NetworkStatusObserver,
    ) : AuthInitializationComponent.Factory {

        override fun invoke(
            componentContext: ComponentContext,
            role: UserRole,
            phoneNumber: String,
            tradingStationId: Int?,
            proceed: () -> Unit,
        ) = ActualAuthInitializationComponent(
            componentContext = componentContext,
            role = role,
            phoneNumber = phoneNumber,
            tradingStationId = tradingStationId,
            proceed = proceed,
            authorizeUserUseCase = authorizeUserUseCase,
            initializeTradingStationsUseCase = initializeTradingStationsUseCase,
            initializeCatalogUseCase = initializeCatalogUseCase,
            initializeCurrentOrderUseCase = initializeCurrentOrderUseCase,
            networkStatusObserver = networkStatusObserver,
        )
    }

    class StateHolder(
        role: UserRole,
        networkStatusObserver: NetworkStatusObserver,
    ) : BaseStateHolder() {

        val auth = MutableStateFlow(Status.Queued)

        val tradingStations = MutableStateFlow(
            if (role == UserRole.Nomad) Status.Queued else null,
        )
        val catalog = MutableStateFlow(
            if (role == UserRole.Nomad) Status.Queued else null,
        )
        val currentOrder = MutableStateFlow(
            if (role == UserRole.Nomad) Status.Queued else null,
        )

        val isOnline: StateFlow<Boolean> = networkStatusObserver.observe()
            .stateIn(scope, SharingStarted.Eagerly, false)
    }

    private companion object {

        const val RETRY_DELAY_MS = 10_000L
    }
}

class MockAuthInitializationComponent(
    authStatus: Status = Status.Done,
    tradingStationsStatus: Status? = Status.Processing,
    catalogStatus: Status? = Status.Queued,
    currentOrderStatus: Status? = Status.Queued,
) : AuthInitializationComponent {

    override val auth = MutableStateFlow(authStatus)
    override val tradingStations = MutableStateFlow(tradingStationsStatus)
    override val catalog = MutableStateFlow(catalogStatus)
    override val currentOrder = MutableStateFlow(currentOrderStatus)
}
