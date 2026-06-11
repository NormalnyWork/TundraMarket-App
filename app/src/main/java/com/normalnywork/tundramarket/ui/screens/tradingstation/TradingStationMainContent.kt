package com.normalnywork.tundramarket.ui.screens.tradingstation

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.minus
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.entities.TradingStationOrdersPage
import com.normalnywork.tundramarket.domain.entities.denialComment
import com.normalnywork.tundramarket.ui.kit.components.TMButtonPrimary
import com.normalnywork.tundramarket.ui.kit.components.TMTopBar
import com.normalnywork.tundramarket.ui.kit.icons.Checkmark
import com.normalnywork.tundramarket.ui.kit.icons.ChevronRight
import com.normalnywork.tundramarket.ui.kit.icons.LatitudeFilled
import com.normalnywork.tundramarket.ui.kit.icons.LongitudeFilled
import com.normalnywork.tundramarket.ui.kit.icons.Processing
import com.normalnywork.tundramarket.ui.kit.icons.Rejected
import com.normalnywork.tundramarket.ui.kit.icons.Sent
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.Waiting
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import com.normalnywork.tundramarket.ui.tools.toDisplayCoordinate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.days

@Composable
fun TradingStationMainContent(component: TradingStationMainComponent) {
    val pages by component.childPages.subscribeAsState()
    val hasNewOrders by component.hasNewOrders.collectAsState()
    val colors = LocalTMColors.current

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.background),
            ) {
                TMTopBar(title = stringResource(R.string.app_name))
                TradingStationTabs(
                    pages = pages.items.map { child -> child.configuration },
                    selectedIndex = pages.selectedIndex,
                    hasNewOrders = hasNewOrders,
                    onPageSelected = component::onPageSelected,
                )
            }
        },
        containerColor = colors.background,
        modifier = Modifier.fillMaxSize(),
    ) { paddings ->
        val actualPaddings = paddings - PaddingValues(top = paddings.calculateTopPadding())

        ChildPages(
            pages = component.childPages,
            onPageSelected = component::onPageSelected,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddings.calculateTopPadding()),
            scrollAnimation = PagesScrollAnimation.Default,
        ) { _, pageComponent ->
            val orders = pageComponent.orders.collectAsLazyPagingItems()

            TradingStationOrdersPageContent(
                page = pageComponent.page,
                orders = orders,
                onOpenNewOrdersClick = component::onNewOrdersClicked,
                onOrderClick = pageComponent::onOpenOrderDetailsClicked,
                paddings = actualPaddings,
            )
        }
    }
}

@Composable
private fun TradingStationOrdersPageContent(
    page: TradingStationOrdersPage,
    orders: LazyPagingItems<Order>,
    onOpenNewOrdersClick: () -> Unit,
    onOrderClick: (Order) -> Unit,
    paddings: PaddingValues,
) {
    Crossfade(
        targetState = orders.toScreenState(),
        label = "TradingStationOrdersPageState",
        modifier = Modifier.fillMaxSize(),
    ) { state ->
        when (state) {
            OrdersPageScreenState.Loading -> TradingStationOrdersLoadingState(
                paddings = paddings,
            )

            OrdersPageScreenState.Empty -> TradingStationOrdersEmptyState(
                page = page,
                onOpenNewOrdersClick = onOpenNewOrdersClick,
                paddings = paddings,
            )

            OrdersPageScreenState.Orders -> TradingStationOrdersList(
                orders = orders,
                onOrderClick = onOrderClick,
                paddings = paddings,
            )
        }
    }
}

@Composable
private fun TradingStationTabs(
    pages: List<TradingStationOrdersPage>,
    selectedIndex: Int,
    hasNewOrders: Boolean,
    onPageSelected: (Int) -> Unit,
) {
    val colors = LocalTMColors.current

    PrimaryTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = colors.background,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier
                    .tabIndicatorOffset(selectedIndex, matchContentSize = true),
                width = Dp.Unspecified,
                color = colors.primary,
            )
        },
        divider = {
            HorizontalDivider(color = colors.stroke)
        }
    ) {
        pages.forEachIndexed { index, page ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onPageSelected(index) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(page.titleRes()),
                            style = LocalTMTypography.current.caption,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        AnimatedVisibility(
                            visible = page == TradingStationOrdersPage.New
                                    && hasNewOrders
                                    && index != selectedIndex
                        ) {
                            Badge(
                                containerColor = colors.primary,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                },
                selectedContentColor = colors.primary,
                unselectedContentColor = colors.textSecondary,
            )
        }
    }
}

@Composable
private fun TradingStationOrdersList(
    orders: LazyPagingItems<Order>,
    onOrderClick: (Order) -> Unit,
    paddings: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp) + paddings,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            count = orders.itemCount,
            key = { index -> orders[index]?.id ?: index },
        ) { index ->
            val order = orders[index]
            if (order != null) {
                TradingStationOrderCard(
                    order = order,
                    onClick = { onOrderClick(order) },
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TradingStationOrdersLoadingState(paddings: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddings),
        contentAlignment = Alignment.Center,
    ) {
        LoadingIndicator(color = LocalTMColors.current.primary)
    }
}

@Composable
private fun TradingStationOrdersEmptyState(
    page: TradingStationOrdersPage,
    onOpenNewOrdersClick: () -> Unit,
    paddings: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddings),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically),
    ) {
        Text(
            text = stringResource(R.string.trading_station_orders_empty_title).uppercase(),
            style = LocalTMTypography.current.label,
            color = LocalTMColors.current.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(page.emptyBodyRes()),
            style = LocalTMTypography.current.body,
            color = LocalTMColors.current.textSecondary,
            textAlign = TextAlign.Center,
        )
        AnimatedVisibility(visible = page == TradingStationOrdersPage.Active) {
            TMButtonPrimary(
                text = stringResource(R.string.trading_station_orders_empty_active_action),
                onClick = onOpenNewOrdersClick,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

@Composable
private fun TradingStationOrderCard(
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
            Text(
                text = stringResource(R.string.trading_station_order_title, order.displayId).uppercase(),
                style = typography.label,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            TradingStationOrderLocationRow(order = order)
        }

        TradingStationOrderStatusCard(order = order)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.trading_station_order_details_action),
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
private fun TradingStationOrderLocationRow(order: Order) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TradingStationOrderInfoItem(
            icon = TMIcons.LatitudeFilled,
            text = order.location.latitude.toDisplayCoordinate(),
        )
        TradingStationOrderInfoItem(
            icon = TMIcons.LongitudeFilled,
            text = order.location.longitude.toDisplayCoordinate(),
        )
    }
}

@Composable
private fun TradingStationOrderInfoItem(
    icon: ImageVector,
    text: String,
) {
    Row(
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
private fun TradingStationOrderStatusCard(order: Order) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TMShapes.Medium)
            .background(colors.backgroundCard)
            .animateContentSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp),
        ) {
            Icon(
                imageVector = order.status.statusIcon(),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(24.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.trading_station_order_status_label).uppercase(),
                    style = typography.captionLabel,
                    color = colors.textSecondary,
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(order.status.titleRes()).uppercase(),
                        style = typography.label,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = order.statusBody(),
                        style = typography.body,
                        color = colors.textPrimary,
                    )
                }
            }
        }
        if (order.status == OrderStatus.Denied && order.statusHistory.denialComment != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colors.background,
                        shape = TMShapes.Small,
                    )
                    .border(
                        width = 1.dp,
                        color = colors.stroke,
                        shape = TMShapes.Small,
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = order.statusHistory.denialComment!!,
                    style = typography.body,
                    color = colors.textPrimary,
                )
            }
        }
    }
}

private fun LazyPagingItems<Order>.toScreenState(): OrdersPageScreenState {
    return when {
        itemCount > 0 -> OrdersPageScreenState.Orders
        loadState.refresh is LoadState.Loading -> OrdersPageScreenState.Loading
        else -> OrdersPageScreenState.Empty
    }
}

private enum class OrdersPageScreenState {
    Loading,
    Empty,
    Orders,
}

private fun TradingStationOrdersPage.titleRes() = when (this) {
    TradingStationOrdersPage.Active -> R.string.trading_station_orders_tab_active
    TradingStationOrdersPage.New -> R.string.trading_station_orders_tab_new
    TradingStationOrdersPage.History -> R.string.trading_station_orders_tab_history
}

private fun TradingStationOrdersPage.emptyBodyRes() = when (this) {
    TradingStationOrdersPage.Active -> R.string.trading_station_orders_empty_active_body
    TradingStationOrdersPage.New -> R.string.trading_station_orders_empty_new_body
    TradingStationOrdersPage.History -> R.string.trading_station_orders_empty_history_body
}

private fun OrderStatus.titleRes() = when (this) {
    OrderStatus.Created -> R.string.trading_station_order_status_created_title
    OrderStatus.Processing -> R.string.trading_station_order_status_processing_title
    OrderStatus.Sent -> R.string.trading_station_order_status_sent_title
    OrderStatus.Completed -> R.string.trading_station_order_status_completed_title
    OrderStatus.Cancelled -> R.string.trading_station_order_status_cancelled_title
    OrderStatus.Denied -> R.string.trading_station_order_status_denied_title
}

@Composable
private fun Order.statusBody() = when (status) {
    OrderStatus.Created -> statusHistory.first { it.status == status }.time.toCreatedTime()
    OrderStatus.Processing -> stringResource(
        R.string.trading_station_order_status_processing_body,
        assembledProductIds.size,
        cart.size,
    )
    OrderStatus.Sent -> stringResource(R.string.trading_station_order_status_sent_body)
    OrderStatus.Completed -> stringResource(R.string.trading_station_order_status_completed_body)
    OrderStatus.Cancelled -> stringResource(R.string.trading_station_order_status_cancelled_body)
    OrderStatus.Denied -> stringResource(R.string.trading_station_order_status_denied_body)
}

private fun OrderStatus.statusIcon() = when (this) {
    OrderStatus.Created -> TMIcons.Waiting
    OrderStatus.Processing -> TMIcons.Processing
    OrderStatus.Sent -> TMIcons.Sent
    OrderStatus.Completed -> TMIcons.Checkmark
    OrderStatus.Cancelled,
    OrderStatus.Denied,
    -> TMIcons.Rejected
}

private const val TIME_PATTERN = "H:mm"
private const val YEAR_DATE_PATTERN = "dd.MM.yyyy"
private const val DATE_PATTERN = "dd.MM"

@Composable
private fun Long.toCreatedTime(): String {
    val isToday = DateUtils.isToday(this)
    val isYesterday = DateUtils.isToday(this + 1.days.inWholeMilliseconds)

    val calendar = Calendar.getInstance()
    val itemCalendar = Calendar.getInstance().apply {
        timeInMillis = this@toCreatedTime
    }

    val formattedTime = SimpleDateFormat(TIME_PATTERN, LocalLocale.current.platformLocale)
        .format(Date(this))

    val pattern = if (calendar.get(Calendar.YEAR) == itemCalendar.get(Calendar.YEAR))
        DATE_PATTERN else YEAR_DATE_PATTERN
    val formattedDate = SimpleDateFormat(pattern, LocalLocale.current.platformLocale)
        .format(Date(this))

    val day = when {
        isToday -> stringResource(R.string.trading_station_order_status_created_today)
        isYesterday -> stringResource(R.string.trading_station_order_status_created_yesterday)
        else -> formattedDate
    }

    return stringResource(R.string.trading_station_order_status_created_body, day, formattedTime)
}
