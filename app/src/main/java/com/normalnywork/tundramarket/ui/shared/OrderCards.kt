package com.normalnywork.tundramarket.ui.shared

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.domain.entities.Location
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatusHistory
import com.normalnywork.tundramarket.domain.entities.Product
import com.normalnywork.tundramarket.domain.entities.TradingStation
import com.normalnywork.tundramarket.ui.kit.icons.Checkmark
import com.normalnywork.tundramarket.ui.kit.icons.ChevronRight
import com.normalnywork.tundramarket.ui.kit.icons.Comment
import com.normalnywork.tundramarket.ui.kit.icons.Latitude
import com.normalnywork.tundramarket.ui.kit.icons.LatitudeFilled
import com.normalnywork.tundramarket.ui.kit.icons.Location
import com.normalnywork.tundramarket.ui.kit.icons.Longitude
import com.normalnywork.tundramarket.ui.kit.icons.LongitudeFilled
import com.normalnywork.tundramarket.ui.kit.icons.Processing
import com.normalnywork.tundramarket.ui.kit.icons.Products
import com.normalnywork.tundramarket.ui.kit.icons.Rejected
import com.normalnywork.tundramarket.ui.kit.icons.Sent
import com.normalnywork.tundramarket.ui.kit.icons.Shop
import com.normalnywork.tundramarket.ui.kit.icons.ShopFilled
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.Waiting
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import com.normalnywork.tundramarket.ui.tools.toDisplayCoordinate
import com.normalnywork.tundramarket.utils.CoordinateDistanceCalculator.distanceTo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.days

@Composable
fun HistoryOrderCard(
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

@Composable
fun OrderStatusHistoryCard(history: List<OrderStatusHistory>) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current
    val sortedHistory = history.sortedBy { it.time }
    var animateHistoryChanges by remember { mutableStateOf(false) }

    LaunchedEffect(sortedHistory.isNotEmpty()) {
        if (sortedHistory.isNotEmpty()) {
            animateHistoryChanges = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colors.background,
                shape = TMShapes.Medium,
            )
            .border(
                width = 1.dp,
                color = colors.stroke,
                shape = TMShapes.Medium,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.nomad_main_status_history_title).uppercase(),
            style = typography.title,
            color = colors.textPrimary,
        )
        Column(modifier = if (animateHistoryChanges) Modifier.animateContentSize() else Modifier) {
            sortedHistory.forEachIndexed { index, item ->
                val isLatest = index == sortedHistory.lastIndex

                key(item.status) {
                    AnimatedOrderStatusHistoryItem(
                        item = item,
                        isLatest = isLatest,
                        animateEnter = animateHistoryChanges,
                    )
                }

                if (!isLatest) {
                    key(item.status, "divider") {
                        AnimatedOrderStatusHistoryDivider(animateEnter = animateHistoryChanges)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedOrderStatusHistoryItem(
    item: OrderStatusHistory,
    isLatest: Boolean,
    animateEnter: Boolean,
) {
    val visibleState = remember(item.status) {
        MutableTransitionState(!animateEnter).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
    ) {
        OrderStatusHistoryItem(
            item = item,
            isLatest = isLatest,
        )
    }
}

@Composable
private fun OrderStatusHistoryItem(
    item: OrderStatusHistory,
    isLatest: Boolean,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current
    val iconColors = item.status.toStatusHistoryIconColors(isLatest = isLatest)
    val iconBackgroundColor by animateColorAsState(
        targetValue = iconColors.backgroundColor,
        label = "StatusHistoryIconBackgroundColor",
    )
    val iconContentColor by animateColorAsState(
        targetValue = iconColors.contentColor,
        label = "StatusHistoryIconContentColor",
    )
    val statusTime = item.time.toStatusHistoryTime()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(StatusHistoryTokens.IconContainerSize)
                .background(
                    color = iconBackgroundColor,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = item.status.toStatusHistoryIcon(),
                contentDescription = null,
                tint = iconContentColor,
                modifier = Modifier.size(StatusHistoryTokens.IconSize),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = item.status.toStatusHistoryTitle(),
                style = typography.subtitle,
                color = colors.textPrimary,
            )
            AnimatedContent(
                targetState = statusTime,
                transitionSpec = {
                    fadeIn() + slideInVertically { -it / 2 } togetherWith
                        fadeOut() + slideOutVertically { it / 2 }
                },
                label = "StatusHistoryTime",
            ) { animatedStatusTime ->
                Text(
                    text = animatedStatusTime,
                    style = typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun AnimatedOrderStatusHistoryDivider(animateEnter: Boolean) {
    val visibleState = remember {
        MutableTransitionState(!animateEnter).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
    ) {
        OrderStatusHistoryDivider()
    }
}

@Composable
private fun OrderStatusHistoryDivider() {
    val color = LocalTMColors.current.primaryVariant

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier.size(
                width = StatusHistoryTokens.IconContainerSize,
                height = StatusHistoryTokens.DividerHeight,
            ),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(
                modifier = Modifier.size(
                    width = StatusHistoryTokens.DividerWidth,
                    height = StatusHistoryTokens.DividerHeight,
                ),
            ) {
                drawLine(
                    color = color,
                    start = Offset(x = size.width / 2f, y = 0f),
                    end = Offset(x = size.width / 2f, y = size.height),
                    strokeWidth = size.width,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(
                            StatusHistoryTokens.DashLength.toPx(),
                            StatusHistoryTokens.DashGap.toPx(),
                        ),
                    ),
                )
            }
        }
    }
}

@Composable
private fun OrderStatus.toStatusHistoryTitle(): String =
    when (this) {
        OrderStatus.Created -> stringResource(R.string.nomad_main_status_history_created)
        OrderStatus.Processing -> stringResource(R.string.nomad_main_status_history_processing)
        OrderStatus.Sent -> stringResource(R.string.nomad_main_status_history_sent)
        OrderStatus.Completed -> stringResource(R.string.nomad_main_status_history_completed)
        OrderStatus.Cancelled -> stringResource(R.string.nomad_main_status_history_cancelled)
        OrderStatus.Denied -> stringResource(R.string.nomad_main_status_history_denied)
    }

private fun OrderStatus.toStatusHistoryIcon(): ImageVector =
    when (this) {
        OrderStatus.Created -> TMIcons.Waiting
        OrderStatus.Processing -> TMIcons.Processing
        OrderStatus.Sent -> TMIcons.Sent
        OrderStatus.Completed -> TMIcons.Checkmark
        OrderStatus.Cancelled,
        OrderStatus.Denied -> TMIcons.Rejected
    }

@Composable
private fun OrderStatus.toStatusHistoryIconColors(isLatest: Boolean): StatusHistoryIconColors {
    val colors = LocalTMColors.current

    return if (isLatest) {
        StatusHistoryIconColors(
            backgroundColor = colors.primary,
            contentColor = colors.onPrimary,
        )
    } else {
        StatusHistoryIconColors(
            backgroundColor = colors.primaryVariant,
            contentColor = colors.primary,
        )
    }
}

private const val TODAY_TIME_PATTERN = "H:mm"
private const val YEAR_DATE_TIME_PATTERN = "dd.MM.yyyy, H:mm"
private const val DATE_TIME_PATTERN = "dd.MM, H:mm"

@Composable
private fun Long.toStatusHistoryTime(): String {
    val isToday = DateUtils.isToday(this)
    val isYesterday = DateUtils.isToday(this + 1.days.inWholeMilliseconds)

    val calendar = Calendar.getInstance()
    val itemCalendar = Calendar.getInstance().apply {
        timeInMillis = this@toStatusHistoryTime
    }

    val pattern = when {
        isToday || isYesterday -> TODAY_TIME_PATTERN
        calendar.get(Calendar.YEAR) == itemCalendar.get(Calendar.YEAR) -> DATE_TIME_PATTERN
        else -> YEAR_DATE_TIME_PATTERN
    }
    val formattedTime = SimpleDateFormat(pattern, LocalLocale.current.platformLocale)
        .format(Date(this))

    return when {
        isToday -> stringResource(R.string.nomad_main_status_history_today_time, formattedTime)
        isYesterday -> stringResource(R.string.nomad_main_status_history_yesterday_time, formattedTime)
        else -> formattedTime
    }
}

private data class StatusHistoryIconColors(
    val backgroundColor: Color,
    val contentColor: Color,
)

private object StatusHistoryTokens {

    val IconContainerSize = 40.dp
    val IconSize = 24.dp
    val DividerHeight = 12.dp
    val DividerWidth = 2.dp
    val DashLength = 2.dp
    val DashGap = 2.dp
}

@Composable
fun OrderDetailsCard(
    cart: List<Pair<Product, Int>>,
    comment: String,
    location: Location? = null,
    tradingStation: TradingStation? = null,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = colors.stroke,
                shape = TMShapes.Medium,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.nomad_main_order_details_title).uppercase(),
            style = typography.title,
            color = colors.textPrimary,
        )
        OrderDetailsSection(
            title = stringResource(R.string.nomad_create_order_overview_products_section),
            icon = TMIcons.Products,
        ) {
            OrderDetailsSurface {
                cart.forEach { (product, quantity) ->
                    ProductRow(
                        product = product,
                        quantity = quantity,
                    )
                }
            }
        }
        OrderDetailsSection(
            title = stringResource(R.string.nomad_create_order_overview_comment_section),
            icon = TMIcons.Comment,
        ) {
            OrderDetailsSurface {
                OrderDetailsText(
                    text = comment.ifEmpty { stringResource(R.string.nomad_create_order_overview_no_comment) },
                    colorSecondary = comment.isEmpty(),
                )
            }
        }
        if (location != null) {
            OrderDetailsSection(
                title = stringResource(R.string.nomad_create_order_overview_location_section),
                icon = TMIcons.Location,
            ) {
                OrderDetailsSurface {
                    OrderDetailsIconTextRow(
                        icon = TMIcons.Latitude,
                        text = location.latitude.toDisplayCoordinate(),
                    )
                    OrderDetailsIconTextRow(
                        icon = TMIcons.Longitude,
                        text = location.longitude.toDisplayCoordinate(),
                    )
                }
            }

            if (tradingStation != null) {
                val stationDistanceKm = tradingStation.location distanceTo location

                OrderDetailsSection(
                    title = stringResource(R.string.nomad_create_order_overview_trading_station_section),
                    icon = TMIcons.Shop,
                ) {
                    OrderDetailsSurface {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OrderDetailsText(
                                text = tradingStation.name,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                            Text(
                                text = stringResource(
                                    R.string.nomad_create_order_trading_station_distance_km,
                                    stationDistanceKm.roundToInt(),
                                ),
                                style = LocalTMTypography.current.bodySmall,
                                color = colors.textSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderDetailsSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val colors = LocalTMColors.current
        val typography = LocalTMTypography.current

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = title,
                style = typography.subtitle,
                color = colors.textPrimary,
            )
        }
        content()
    }
}

@Composable
private fun OrderDetailsSurface(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TMShapes.Medium)
            .background(LocalTMColors.current.backgroundCard)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
private fun ProductRow(
    product: Product,
    quantity: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OrderDetailsText(
            text = product.name,
            modifier = Modifier.weight(1f),
        )
        OrderDetailsText(
            text = stringResource(R.string.nomad_create_order_overview_product_quantity, quantity),
            colorSecondary = true,
        )
    }
}

@Composable
private fun OrderDetailsIconTextRow(
    icon: ImageVector,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LocalTMColors.current.textSecondary,
            modifier = Modifier.size(18.dp),
        )
        OrderDetailsText(text = text)
    }
}

@Composable
private fun OrderDetailsText(
    text: String,
    modifier: Modifier = Modifier,
    colorSecondary: Boolean = false,
) {
    val colors = LocalTMColors.current

    Text(
        text = text,
        style = LocalTMTypography.current.body,
        color = if (colorSecondary) colors.textSecondary else colors.textPrimary,
        modifier = modifier,
    )
}
