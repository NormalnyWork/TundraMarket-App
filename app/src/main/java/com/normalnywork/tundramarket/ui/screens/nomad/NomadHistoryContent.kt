package com.normalnywork.tundramarket.ui.screens.nomad

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.shared.HistoryOrderCard

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
            modifier = Modifier.fillMaxSize(),
        ) { targetState ->
            when (targetState) {
                HistoryScreenState.Loading -> HistoryMessageState(
                    subtitle = stringResource(R.string.nomad_history_loading),
                    loading = true,
                    paddings = paddings,
                )

                HistoryScreenState.Empty -> HistoryMessageState(
                    title = stringResource(R.string.nomad_history_empty_title),
                    body = stringResource(R.string.nomad_history_empty_body),
                    actionText = stringResource(R.string.nomad_history_new_order_action),
                    onActionClick = onNewOrderClick,
                    paddings = paddings,
                )

                HistoryScreenState.NoInternet -> HistoryMessageState(
                    icon = TMIcons.BadConnection,
                    title = stringResource(R.string.nomad_history_no_internet_title),
                    body = stringResource(R.string.nomad_history_no_internet_body),
                    actionText = stringResource(R.string.nomad_history_retry_action),
                    onActionClick = orders::retry,
                    paddings = paddings,
                )

                HistoryScreenState.Orders -> HistoryOrdersState(
                    orders = orders,
                    onOrderClick = onOrderClick,
                    paddings = paddings,
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
    paddings: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(16.dp)
            .padding(paddings),
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
    paddings: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = paddings + PaddingValues(16.dp),
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
                    paddings = paddings,
                )

                HistoryPreviewState.Empty -> HistoryMessageState(
                    title = stringResource(R.string.nomad_history_empty_title),
                    body = stringResource(R.string.nomad_history_empty_body),
                    actionText = stringResource(R.string.nomad_history_new_order_action),
                    onActionClick = onNewOrderClick,
                    paddings = paddings,
                )

                HistoryPreviewState.NoInternet -> HistoryMessageState(
                    icon = TMIcons.BadConnection,
                    title = stringResource(R.string.nomad_history_no_internet_title),
                    body = stringResource(R.string.nomad_history_no_internet_body),
                    actionText = stringResource(R.string.nomad_history_retry_action),
                    onActionClick = onRetryClick,
                    paddings = paddings,
                )

                is HistoryPreviewState.Orders -> HistoryOrdersPreviewState(
                    orders = targetState.orders,
                    onOrderClick = onOrderClick,
                )
            }
        }
    }
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
