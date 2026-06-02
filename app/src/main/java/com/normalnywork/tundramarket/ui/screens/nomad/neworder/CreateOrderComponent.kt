package com.normalnywork.tundramarket.ui.screens.nomad.neworder

import androidx.compose.foundation.text.input.TextFieldState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.normalnywork.tundramarket.domain.entities.Location
import com.normalnywork.tundramarket.domain.entities.Product
import com.normalnywork.tundramarket.domain.entities.TradingStation
import com.normalnywork.tundramarket.domain.usecases.config.GetTradingStationsUseCase
import com.normalnywork.tundramarket.domain.usecases.orders.CreateOrderUseCase
import com.normalnywork.tundramarket.domain.usecases.products.GetCatalogUseCase
import com.normalnywork.tundramarket.ui.tools.BaseStateHolder
import com.normalnywork.tundramarket.utils.CoordinateDistanceCalculator.distanceTo
import com.normalnywork.tundramarket.utils.TMConst
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Singleton

class NomadCreateOrderComponent(
    componentContext: ComponentContext,
    private val onBack: () -> Unit,
    getTradingStationsUseCase: GetTradingStationsUseCase,
    getCatalogUseCase: GetCatalogUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
) : ComponentContext by componentContext {

    private val stateHolder = instanceKeeper.getOrCreate { StateHolder() }

    private val navigation = PagesNavigation<Page>()

    val latitude = stateHolder.latitude

    val longitude = stateHolder.longitude

    val comment = stateHolder.comment

    val tradingStations: StateFlow<List<TradingStation>> = getTradingStationsUseCase()
        .stateIn(stateHolder.scope, SharingStarted.Lazily, emptyList())

    val catalog: StateFlow<List<Product>> = getCatalogUseCase()
        .stateIn(stateHolder.scope, SharingStarted.Lazily, emptyList())

    val selectedTradingStation = stateHolder.selectedTradingStation

    val selectedProductQuantities = stateHolder.selectedProductQuantities

    val childPages: Value<ChildPages<Page, PageComponent>> = childPages(
        source = navigation,
        serializer = Page.serializer(),
        initialPages = {
            Pages(
                items = listOf(
                    Page.Location,
                    Page.TradingStation,
                    Page.Products,
                    Page.Comment,
                    Page.Overview,
                ),
                selectedIndex = 0,
            )
        },
        childFactory = ::child,
    )

    init {
        backHandler.register(
            BackCallback {
                goBack()
            }
        )
    }

    fun onBackClicked() {
        goBack()
    }

    fun onNextPageClicked() {
        val pages = childPages.value
        when (pages.items[pages.selectedIndex].configuration) {
            Page.Location -> if (!canProceedFromLocation()) return
            Page.TradingStation -> if (!canProceedFromTradingStation()) return
            Page.Products -> if (!canProceedFromProducts()) return
            Page.Comment,
            Page.Overview -> Unit
        }

        if (pages.selectedIndex < pages.items.lastIndex) {
            navigation.select(pages.selectedIndex + 1)
        }
    }

    fun onTradingStationClicked(tradingStation: TradingStation) {
        selectedTradingStation.value = tradingStation
    }

    fun onProductIncremented(product: Product) {
        val current = selectedProductQuantities.value
        selectedProductQuantities.value = current + (product.id to ((current[product.id] ?: 0) + 1))
    }

    fun onProductDecremented(product: Product) {
        val current = selectedProductQuantities.value
        val newQuantity = (current[product.id] ?: 0) - 1
        selectedProductQuantities.value = if (newQuantity > 0) {
            current + (product.id to newQuantity)
        } else {
            current - product.id
        }
    }

    fun onSkipCommentClicked() {
        onNextPageClicked()
    }

    fun onCreateOrderClicked() {
        if (stateHolder.isCreatingOrder.value) return

        val location = currentLocation() ?: return
        val tradingStation = selectedTradingStation.value ?: return
        val productQuantities = selectedProductQuantities.value
        val cart = catalog.value.mapNotNull { product ->
            productQuantities[product.id]
                ?.takeIf { quantity -> quantity > 0 }
                ?.let { quantity -> product to quantity }
        }

        if (cart.isEmpty()) return

        stateHolder.isCreatingOrder.value = true
        stateHolder.scope.launch {
            try {
                createOrderUseCase(
                    tradingStation = tradingStation,
                    cart = cart,
                    location = location,
                    comment = comment.text.toString().trim(),
                )
                onBack.invoke()
            } finally {
                stateHolder.isCreatingOrder.value = false
            }
        }
    }

    fun onPageSelected(index: Int) {
        val pages = childPages.value
        if (index > pages.selectedIndex && !canProceedToPage(index)) {
            return
        }

        navigation.select(index)
    }

    fun canProceedFromLocation(): Boolean {
        return currentLocation() != null
    }

    private fun goBack() {
        val pages = childPages.value
        if (pages.selectedIndex > 0) {
            navigation.select(pages.selectedIndex - 1)
        } else {
            onBack.invoke()
        }
    }

    private fun canProceedFromTradingStation(): Boolean {
        val location = currentLocation() ?: return false
        val selected = selectedTradingStation.value ?: return false

        return selected.location distanceTo location <= TMConst.TRADING_STATION_REACH_KM
    }

    private fun canProceedToPage(index: Int): Boolean {
        val pages = childPages.value
        return pages.items
            .take(index)
            .all { canProceedFromPage(it.configuration) }
    }

    private fun canProceedFromPage(page: Page): Boolean {
        return when (page) {
            Page.Location -> canProceedFromLocation()
            Page.TradingStation -> canProceedFromTradingStation()
            Page.Products -> canProceedFromProducts()
            Page.Comment,
            Page.Overview -> true
        }
    }

    private fun canProceedFromProducts(): Boolean {
        return selectedProductQuantities.value.values.any { it > 0 }
    }

    private fun currentLocation(): Location? {
        val latitude = latitude.text.toString().replace(',', '.').toFloatOrNull()
        val longitude = longitude.text.toString().replace(',', '.').toFloatOrNull()

        return if (
            latitude != null &&
            longitude != null &&
            latitude in MIN_LATITUDE..MAX_LATITUDE &&
            longitude in MIN_LONGITUDE..MAX_LONGITUDE
        ) {
            Location(latitude = latitude, longitude = longitude)
        } else {
            null
        }
    }

    private fun child(
        page: Page,
        componentContext: ComponentContext,
    ): PageComponent = PageComponent(
        componentContext = componentContext,
        page = page,
    )

    @Serializable
    enum class Page {
        Location,
        TradingStation,
        Products,
        Comment,
        Overview,
    }

    class PageComponent(
        componentContext: ComponentContext,
        val page: Page,
    ) : ComponentContext by componentContext

    class StateHolder : BaseStateHolder() {

        val latitude = TextFieldState()
        val longitude = TextFieldState()
        val comment = TextFieldState()
        val selectedTradingStation = MutableStateFlow<TradingStation?>(null)
        val selectedProductQuantities = MutableStateFlow<Map<Int, Int>>(emptyMap())
        val isCreatingOrder = MutableStateFlow(false)
    }

    private companion object {

        const val MIN_LATITUDE = -90f
        const val MAX_LATITUDE = 90f
        const val MIN_LONGITUDE = -180f
        const val MAX_LONGITUDE = 180f
    }

    @Singleton
    class Factory(
        private val getTradingStationsUseCase: GetTradingStationsUseCase,
        private val getCatalogUseCase: GetCatalogUseCase,
        private val createOrderUseCase: CreateOrderUseCase,
    ) {

        operator fun invoke(
            componentContext: ComponentContext,
            onBack: () -> Unit,
        ) = NomadCreateOrderComponent(
            componentContext = componentContext,
            onBack = onBack,
            getTradingStationsUseCase = getTradingStationsUseCase,
            getCatalogUseCase = getCatalogUseCase,
            createOrderUseCase = createOrderUseCase,
        )
    }
}
