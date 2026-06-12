package com.normalnywork.tundramarket.ui.screens.nomad.neworder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.domain.entities.Location
import com.normalnywork.tundramarket.domain.entities.Product
import com.normalnywork.tundramarket.domain.entities.TradingStation
import com.normalnywork.tundramarket.ui.kit.components.InfoCard
import com.normalnywork.tundramarket.ui.kit.components.StageBar
import com.normalnywork.tundramarket.ui.kit.components.TMButtonPrimary
import com.normalnywork.tundramarket.ui.kit.components.TMButtonSecondary
import com.normalnywork.tundramarket.ui.kit.components.TMButtonSlider
import com.normalnywork.tundramarket.ui.kit.components.TMTopBar
import com.normalnywork.tundramarket.ui.kit.icons.Checkmark
import com.normalnywork.tundramarket.ui.kit.icons.Comment
import com.normalnywork.tundramarket.ui.kit.icons.Location
import com.normalnywork.tundramarket.ui.kit.icons.Products
import com.normalnywork.tundramarket.ui.kit.icons.Shop
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.utils.CoordinateDistanceCalculator.distanceTo

@Composable
fun NomadCreateOrderContent(component: NomadCreateOrderComponent) {
    val colors = LocalTMColors.current
    val pages by component.childPages.subscribeAsState()
    val tradingStations by component.tradingStations.collectAsState()
    val catalog by component.catalog.collectAsState()
    val selectedTradingStation by component.selectedTradingStation.collectAsState()
    val selectedProductQuantities by component.selectedProductQuantities.collectAsState()
    val tradingStationItems = tradingStations.rememberDistanceItems(
        latitude = component.latitude.text.toString(),
        longitude = component.longitude.text.toString(),
    )
    val nearestReachableStation = tradingStationItems.firstOrNull { it.reachable }?.tradingStation

    LaunchedEffect(nearestReachableStation, selectedTradingStation, tradingStationItems) {
        if (nearestReachableStation != null &&
            tradingStationItems.none { it.tradingStation == selectedTradingStation && it.reachable }
        ) {
            component.onTradingStationClicked(nearestReachableStation)
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.background),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TMTopBar(
                    title = stringResource(R.string.nomad_create_order_title),
                    onBack = component::onBackClicked,
                )
                StageBar(
                    range = 1..pages.items.size,
                    current = pages.selectedIndex + 1,
                    modifier = Modifier.padding(vertical = 12.dp),
                    updateStage = { component.onPageSelected(it - 1) },
                )
            }
        },
        content = { paddings ->
            ChildPages(
                pages = component.childPages,
                onPageSelected = component::onPageSelected,
                pager = { modifier, state, key, pageContent ->
                    HorizontalPager(
                        state = state,
                        modifier = modifier,
                        key = key,
                        userScrollEnabled = false,
                        pageContent = pageContent,
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings),
                scrollAnimation = PagesScrollAnimation.Default,
            ) { _, pageComponent ->
                NomadCreateOrderPageContent(
                    component = component,
                    pageComponent = pageComponent,
                    catalog = catalog,
                    selectedProductQuantities = selectedProductQuantities,
                    tradingStationItems = tradingStationItems,
                    selectedTradingStation = selectedTradingStation,
                    onTradingStationClick = component::onTradingStationClicked,
                    onProductIncrement = component::onProductIncremented,
                    onProductDecrement = component::onProductDecremented,
                    onNext = component::onNextPageClicked,
                    onSkipComment = component::onSkipCommentClicked,
                    onCreateOrder = component::onCreateOrderClicked,
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
    )
}

@Composable
private fun NomadCreateOrderPageContent(
    component: NomadCreateOrderComponent,
    pageComponent: NomadCreateOrderComponent.PageComponent,
    catalog: List<Product>,
    selectedProductQuantities: Map<Int, Int>,
    tradingStationItems: List<TradingStationDistance>,
    selectedTradingStation: TradingStation?,
    onTradingStationClick: (TradingStation) -> Unit,
    onProductIncrement: (Product) -> Unit,
    onProductDecrement: (Product) -> Unit,
    onNext: () -> Unit,
    onSkipComment: () -> Unit,
    onCreateOrder: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()
        val canContinue = when (pageComponent.page) {
            NomadCreateOrderComponent.Page.Location -> component.canProceedFromLocation()
            NomadCreateOrderComponent.Page.TradingStation ->
                tradingStationItems.any { item ->
                    item.tradingStation == selectedTradingStation && item.reachable
                }
            NomadCreateOrderComponent.Page.Products -> selectedProductQuantities.values.any { it > 0 }
            NomadCreateOrderComponent.Page.Comment,
            NomadCreateOrderComponent.Page.Overview -> true
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            InfoCard(
                title = pageComponent.page.infoTitle(),
                body = pageComponent.page.infoBody(),
                icon = pageComponent.page.icon(),
            )
            NomadCreateOrderPageBody(
                component = component,
                page = pageComponent.page,
                catalog = catalog,
                selectedProductQuantities = selectedProductQuantities,
                tradingStationItems = tradingStationItems,
                selectedTradingStation = selectedTradingStation,
                onTradingStationClick = onTradingStationClick,
                onProductIncrement = onProductIncrement,
                onProductDecrement = onProductDecrement,
            )
        }
        NomadCreateOrderPageActions(
            page = pageComponent.page,
            showDivider = scrollState.canScrollForward,
            enabled = canContinue,
            onNext = onNext,
            onSkipComment = onSkipComment,
            onCreateOrder = onCreateOrder,
        )
    }
}

@Composable
private fun NomadCreateOrderPageBody(
    component: NomadCreateOrderComponent,
    page: NomadCreateOrderComponent.Page,
    catalog: List<Product>,
    selectedProductQuantities: Map<Int, Int>,
    tradingStationItems: List<TradingStationDistance>,
    selectedTradingStation: TradingStation?,
    onTradingStationClick: (TradingStation) -> Unit,
    onProductIncrement: (Product) -> Unit,
    onProductDecrement: (Product) -> Unit,
) {
    when (page) {
        NomadCreateOrderComponent.Page.Location -> LocationPageContent(component = component)

        NomadCreateOrderComponent.Page.TradingStation -> TradingStationPageContent(
            items = tradingStationItems,
            selectedTradingStation = selectedTradingStation,
            onTradingStationClick = onTradingStationClick,
        )

        NomadCreateOrderComponent.Page.Products -> ProductsPageContent(
            products = catalog,
            selectedQuantities = selectedProductQuantities,
            onIncrement = onProductIncrement,
            onDecrement = onProductDecrement,
        )

        NomadCreateOrderComponent.Page.Comment -> CommentPageContent(comment = component.comment)

        NomadCreateOrderComponent.Page.Overview -> OverviewPageContent(
            location = parseCoordinates(
                latitude = component.latitude.text.toString(),
                longitude = component.longitude.text.toString(),
            ),
            tradingStation = selectedTradingStation,
            tradingStationDistanceKm = tradingStationItems
                .firstOrNull { it.tradingStation == selectedTradingStation }
                ?.distanceKm,
            products = catalog,
            selectedQuantities = selectedProductQuantities,
            comment = component.comment,
        )
    }
}

@Composable
private fun NomadCreateOrderPageActions(
    page: NomadCreateOrderComponent.Page,
    showDivider: Boolean,
    enabled: Boolean,
    onNext: () -> Unit,
    onSkipComment: () -> Unit,
    onCreateOrder: () -> Unit,
) {
    Column {
        val colors = LocalTMColors.current
        val dividerColor = if (showDivider) colors.stroke else Color.Transparent

        HorizontalDivider(color = dividerColor)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (page) {
                NomadCreateOrderComponent.Page.Location,
                NomadCreateOrderComponent.Page.TradingStation,
                NomadCreateOrderComponent.Page.Products -> {
                    TMButtonPrimary(
                        text = stringResource(R.string.nomad_create_order_next_action),
                        onClick = onNext,
                        enabled = enabled,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                NomadCreateOrderComponent.Page.Comment -> {
                    TMButtonSecondary(
                        text = stringResource(R.string.nomad_create_order_skip_action),
                        onClick = onSkipComment,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    TMButtonPrimary(
                        text = stringResource(R.string.nomad_create_order_next_action),
                        onClick = onNext,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                NomadCreateOrderComponent.Page.Overview -> TMButtonSlider(
                    text = stringResource(R.string.nomad_create_order_create_action),
                    onClick = onCreateOrder,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun List<TradingStation>.rememberDistanceItems(
    latitude: String,
    longitude: String,
): List<TradingStationDistance> {
    return remember(this, latitude, longitude) {
        val location = parseCoordinates(latitude, longitude)

        map { station ->
            TradingStationDistance(
                tradingStation = station,
                distanceKm = location?.let { station.location distanceTo it },
            )
        }.sortedBy { it.distanceKm ?: Float.MAX_VALUE }
    }
}

private fun parseCoordinates(
    latitude: String,
    longitude: String,
): Location? {
    val parsedLatitude = latitude.replace(',', '.').toFloatOrNull()
    val parsedLongitude = longitude.replace(',', '.').toFloatOrNull()

    return if (parsedLatitude != null && parsedLongitude != null) {
        Location(
            latitude = parsedLatitude,
            longitude = parsedLongitude,
        )
    } else {
        null
    }
}

@Composable
private fun NomadCreateOrderComponent.Page.infoTitle(): String {
    return stringResource(
        when (this) {
            NomadCreateOrderComponent.Page.Location -> R.string.nomad_create_order_location_title
            NomadCreateOrderComponent.Page.TradingStation -> R.string.nomad_create_order_trading_station_title
            NomadCreateOrderComponent.Page.Products -> R.string.nomad_create_order_products_title
            NomadCreateOrderComponent.Page.Comment -> R.string.nomad_create_order_comment_title
            NomadCreateOrderComponent.Page.Overview -> R.string.nomad_create_order_overview_title
        },
    )
}

@Composable
private fun NomadCreateOrderComponent.Page.infoBody(): String {
    return stringResource(
        when (this) {
            NomadCreateOrderComponent.Page.Location -> R.string.nomad_create_order_location_body
            NomadCreateOrderComponent.Page.TradingStation -> R.string.nomad_create_order_trading_station_body
            NomadCreateOrderComponent.Page.Products -> R.string.nomad_create_order_products_body
            NomadCreateOrderComponent.Page.Comment -> R.string.nomad_create_order_comment_body
            NomadCreateOrderComponent.Page.Overview -> R.string.nomad_create_order_overview_body
        },
    )
}

private fun NomadCreateOrderComponent.Page.icon(): ImageVector {
    return when (this) {
        NomadCreateOrderComponent.Page.Location -> TMIcons.Location
        NomadCreateOrderComponent.Page.TradingStation -> TMIcons.Shop
        NomadCreateOrderComponent.Page.Products -> TMIcons.Products
        NomadCreateOrderComponent.Page.Comment -> TMIcons.Comment
        NomadCreateOrderComponent.Page.Overview -> TMIcons.Checkmark
    }
}
