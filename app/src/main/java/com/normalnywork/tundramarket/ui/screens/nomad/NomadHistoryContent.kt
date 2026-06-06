package com.normalnywork.tundramarket.ui.screens.nomad

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.domain.entities.Location
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatusHistory
import com.normalnywork.tundramarket.domain.entities.Product
import com.normalnywork.tundramarket.domain.entities.TradingStation
import com.normalnywork.tundramarket.ui.kit.components.TMButtonPrimary
import com.normalnywork.tundramarket.ui.kit.components.TMTopBar
import com.normalnywork.tundramarket.ui.kit.icons.BadConnection
import com.normalnywork.tundramarket.ui.kit.icons.Checkmark
import com.normalnywork.tundramarket.ui.kit.icons.ChevronRight
import com.normalnywork.tundramarket.ui.kit.icons.LatitudeFilled
import com.normalnywork.tundramarket.ui.kit.icons.LongitudeFilled
import com.normalnywork.tundramarket.ui.kit.icons.Processing
import com.normalnywork.tundramarket.ui.kit.icons.Rejected
import com.normalnywork.tundramarket.ui.kit.icons.Sent
import com.normalnywork.tundramarket.ui.kit.icons.ShopFilled
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.Waiting
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import com.normalnywork.tundramarket.ui.tools.toDisplayCoordinate

@Composable
fun NomadHistoryContent(component: NomadHistoryComponent) {
    val orders = component.historyOrders.collectAsLazyPagingItems()

    NomadHistoryContent(
        orders = orders,
        onBackClick = component::onBackClicked,
        onOrderClick = component::onOpenOrderDetailsClicked,
        onNewOrderClick = component::onNewOrderClicked,
    )
}

@Composable
private fun NomadHistoryContent(
    orders: LazyPagingItems<Order>,
    onBackClick: () -> Unit,
    onNewOrderClick: () -> Unit,
    onOrderClick: (Order) -> Unit,
) {
    val screenState = orders.toScreenState()

    Scaffold(
        topBar = {
            TMTopBar(
                title = stringResource(R.string.nomad_history_title),
                onBack = onBackClick,
            )
        },
        containerColor = LocalTMColors.current.background,
        modifier = Modifier.fillMaxSize(),
    ) { paddings ->
        Crossfade(
            targetState = screenState,
            label = "NomadHistoryState",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings),
        ) { targetState ->
            when (targetState) {
                HistoryScreenState.Loading -> HistoryMessageState(
                    subtitle = stringResource(R.string.nomad_history_loading),
                    loading = true,
                )

                HistoryScreenState.Empty -> HistoryMessageState(
                    title = stringResource(R.string.nomad_history_empty_title),
                    body = stringResource(R.string.nomad_history_empty_body),
                    actionText = stringResource(R.string.nomad_history_new_order_action),
                    onActionClick = onNewOrderClick,
                )

                HistoryScreenState.NoInternet -> HistoryMessageState(
                    icon = TMIcons.BadConnection,
                    title = stringResource(R.string.nomad_history_no_internet_title),
                    body = stringResource(R.string.nomad_history_no_internet_body),
                    actionText = stringResource(R.string.nomad_history_retry_action),
                    onActionClick = orders::retry,
                )

                HistoryScreenState.Orders -> HistoryOrdersState(
                    orders = orders,
                    onOrderClick = onOrderClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HistoryMessageState(
    icon: ImageVector? = null,
    title: String? = null,
    body: String? = null,
    subtitle: String? = null,
    loading: Boolean = false,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LocalTMColors.current.primary,
                modifier = Modifier.size(48.dp),
            )
        }
        if (loading) {
            LoadingIndicator(
                color = LocalTMColors.current.primary,
                modifier = Modifier.size(48.dp),
            )
        }
        subtitle?.let {
            Text(
                text = subtitle,
                style = LocalTMTypography.current.subtitle,
                color = LocalTMColors.current.textPrimary,
                textAlign = TextAlign.Center,
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 12.dp),
        ) {
            title?.let {
                Text(
                    text = title.uppercase(),
                    style = LocalTMTypography.current.label,
                    color = LocalTMColors.current.textPrimary,
                    textAlign = TextAlign.Center,
                )
            }
            body?.let {
                Text(
                    text = body,
                    style = LocalTMTypography.current.body,
                    color = LocalTMColors.current.textSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            TMButtonPrimary(
                text = actionText,
                onClick = onActionClick,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun HistoryOrdersState(
    orders: LazyPagingItems<Order>,
    onOrderClick: (Order) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            count = orders.itemCount,
            key = { index -> orders.peek(index)?.id ?: index },
        ) { index ->
            val order = orders[index]
            if (order != null) {
                HistoryOrderCard(
                    order = order,
                    onClick = { onOrderClick(order) },
                    modifier = Modifier.animateItem(),
                )
            }
        }

        when (orders.loadState.append) {
            LoadState.Loading -> item { HistoryLoadingNextPage() }
            is LoadState.Error -> item { HistoryAppendError(onRetryClick = orders::retry) }
            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
private fun HistoryOrdersPreviewState(
    orders: List<Order>,
    onOrderClick: (Order) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            count = orders.size,
            key = { index -> orders[index].id },
        ) { index ->
            val order = orders[index]
            HistoryOrderCard(
                order = order,
                onClick = { onOrderClick(order) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
private fun HistoryLoadingNextPage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = LocalTMColors.current.primary,
            trackColor = LocalTMColors.current.primaryVariant,
        )
    }
}

@Composable
private fun HistoryAppendError(onRetryClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        TMButtonPrimary(
            text = stringResource(R.string.nomad_history_retry_action),
            onClick = onRetryClick,
        )
    }
}

@Composable
private fun HistoryOrderCard(
    order: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(TMShapes.Medium)
            .background(colors.background)
            .border(
                width = 1.dp,
                color = colors.stroke,
                shape = TMShapes.Medium,
            )
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .padding(
                top = 12.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.nomad_history_order_title, order.id).uppercase(),
                    style = typography.label,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                HistoryStatusBadge(status = order.status)
            }
            HistoryOrderLocationRow(order = order)
        }

        HistoryOrderProducts(cart = order.cart)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.nomad_history_open_order_action),
                style = typography.subtitle,
                color = colors.primary,
            )
            Icon(
                imageVector = TMIcons.ChevronRight,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun HistoryStatusBadge(status: OrderStatus) {
    val colors = LocalTMColors.current

    Row(
        modifier = Modifier
            .clip(TMShapes.Medium)
            .background(colors.backgroundCard)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = status.toHistoryIcon(),
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = status.toHistoryTitle().uppercase(),
            style = LocalTMTypography.current.captionLabel,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun HistoryOrderLocationRow(order: Order) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HistoryOrderInfoItem(
            icon = TMIcons.ShopFilled,
            text = order.tradingStation.name.ifBlank {
                stringResource(R.string.nomad_history_trading_station_unknown)
            },
            modifier = Modifier.weight(1f, fill = false),
        )
        HistoryOrderInfoItem(
            icon = TMIcons.LatitudeFilled,
            text = order.location.latitude.toDisplayCoordinate(),
        )
        HistoryOrderInfoItem(
            icon = TMIcons.LongitudeFilled,
            text = order.location.longitude.toDisplayCoordinate(),
        )
    }
}

@Composable
private fun HistoryOrderInfoItem(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LocalTMColors.current.textSecondary,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = text,
            style = LocalTMTypography.current.bodySmall,
            color = LocalTMColors.current.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun HistoryOrderProducts(cart: List<Pair<Product, Int>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TMShapes.Medium)
            .background(LocalTMColors.current.backgroundCard)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        cart.forEach { (product, quantity) ->
            HistoryOrderProductRow(
                product = product,
                quantity = quantity,
            )
        }
    }
}

@Composable
private fun HistoryOrderProductRow(
    product: Product,
    quantity: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = product.name,
            style = LocalTMTypography.current.body,
            color = LocalTMColors.current.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(R.string.nomad_create_order_overview_product_quantity, quantity),
            style = LocalTMTypography.current.bodySmall,
            color = LocalTMColors.current.textSecondary,
            maxLines = 1,
        )
    }
}

@Composable
private fun NomadHistoryPreviewContent(
    state: HistoryPreviewState,
    onBackClick: () -> Unit = {},
    onRetryClick: () -> Unit = {},
    onNewOrderClick: () -> Unit = {},
    onOrderClick: (Order) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TMTopBar(
                title = stringResource(R.string.nomad_history_title),
                onBack = onBackClick,
            )
        },
        containerColor = LocalTMColors.current.background,
        modifier = Modifier.fillMaxSize(),
    ) { paddings ->
        AnimatedContent(
            targetState = state,
            label = "NomadHistoryPreviewState",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings),
        ) { targetState ->
            when (targetState) {
                HistoryPreviewState.Loading -> HistoryMessageState(
                    subtitle = stringResource(R.string.nomad_history_loading),
                    loading = true,
                )

                HistoryPreviewState.Empty -> HistoryMessageState(
                    title = stringResource(R.string.nomad_history_empty_title),
                    body = stringResource(R.string.nomad_history_empty_body),
                    actionText = stringResource(R.string.nomad_history_new_order_action),
                    onActionClick = onNewOrderClick,
                )

                HistoryPreviewState.NoInternet -> HistoryMessageState(
                    icon = TMIcons.BadConnection,
                    title = stringResource(R.string.nomad_history_no_internet_title),
                    body = stringResource(R.string.nomad_history_no_internet_body),
                    actionText = stringResource(R.string.nomad_history_retry_action),
                    onActionClick = onRetryClick,
                )

                is HistoryPreviewState.Orders -> HistoryOrdersPreviewState(
                    orders = targetState.orders,
                    onOrderClick = onOrderClick,
                )
            }
        }
    }
}

@Composable
private fun OrderStatus.toHistoryTitle(): String {
    return when (this) {
        OrderStatus.Created -> stringResource(R.string.nomad_main_status_history_created)
        OrderStatus.Processing -> stringResource(R.string.nomad_main_status_history_processing)
        OrderStatus.Sent -> stringResource(R.string.nomad_main_status_history_sent)
        OrderStatus.Completed -> stringResource(R.string.nomad_main_status_history_completed)
        OrderStatus.Cancelled -> stringResource(R.string.nomad_main_status_history_cancelled)
        OrderStatus.Denied -> stringResource(R.string.nomad_main_status_history_denied)
    }
}

private fun OrderStatus.toHistoryIcon() = when (this) {
    OrderStatus.Processing -> TMIcons.Processing
    OrderStatus.Sent -> TMIcons.Sent
    OrderStatus.Completed -> TMIcons.Checkmark
    OrderStatus.Cancelled,
    OrderStatus.Denied,
    -> TMIcons.Rejected

    else -> TMIcons.Waiting
}

private fun LazyPagingItems<Order>.toScreenState(): HistoryScreenState {
    return when {
        itemCount > 0 -> HistoryScreenState.Orders
        loadState.refresh is LoadState.Loading -> HistoryScreenState.Loading
        loadState.refresh is LoadState.Error -> HistoryScreenState.NoInternet
        else -> HistoryScreenState.Empty
    }
}

private enum class HistoryScreenState {
    Loading,
    Empty,
    NoInternet,
    Orders,
}

private sealed interface HistoryPreviewState {
    data object Loading : HistoryPreviewState
    data object Empty : HistoryPreviewState
    data object NoInternet : HistoryPreviewState

    data class Orders(val orders: List<Order>) : HistoryPreviewState
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun PreviewLoading() {
    NomadHistoryPreviewContent(state = HistoryPreviewState.Loading)
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun PreviewEmpty() {
    NomadHistoryPreviewContent(state = HistoryPreviewState.Empty)
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun PreviewNoInternet() {
    NomadHistoryPreviewContent(state = HistoryPreviewState.NoInternet)
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun PreviewOrders() {
    NomadHistoryPreviewContent(
        state = HistoryPreviewState.Orders(
            orders = listOf(
                sampleHistoryOrder(id = 1042, status = OrderStatus.Completed),
                sampleHistoryOrder(id = 1041, status = OrderStatus.Cancelled),
            ),
        ),
    )
}

private fun sampleHistoryOrder(
    id: Int,
    status: OrderStatus,
) = Order(
    id = id,
    nomadId = 7,
    tradingStation = TradingStation(
        id = 2,
        name = "Паюта",
        phone = null,
        location = Location(
            latitude = 66.86f,
            longitude = 70.83f,
        ),
    ),
    cart = listOf(
        Product(
            id = 1,
            name = "Крупа",
            details = null,
            weight = 1f,
            volume = 1f,
        ) to 3,
        Product(
            id = 2,
            name = "Чай",
            details = null,
            weight = 0.2f,
            volume = 0.4f,
        ) to 2,
    ),
    location = Location(
        latitude = 66.9f,
        longitude = 70.8f,
    ),
    comment = "Доставить к стойбищу у реки",
    status = status,
    statusHistory = listOf(
        OrderStatusHistory(
            status = OrderStatus.Created,
            time = System.currentTimeMillis() - 86_400_000,
        ),
        OrderStatusHistory(
            status = status,
            time = System.currentTimeMillis(),
        ),
    ),
)
