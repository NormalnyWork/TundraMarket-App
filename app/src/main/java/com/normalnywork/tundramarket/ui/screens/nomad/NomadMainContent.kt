package com.normalnywork.tundramarket.ui.screens.nomad

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.domain.entities.OrderNetworkStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatusHistory
import com.normalnywork.tundramarket.ui.kit.components.TMButtonPrimary
import com.normalnywork.tundramarket.ui.kit.components.TMButtonSlider
import com.normalnywork.tundramarket.ui.kit.components.TMButtonTertiary
import com.normalnywork.tundramarket.ui.kit.components.TMCompactTopBar
import com.normalnywork.tundramarket.ui.kit.icons.BadConnection
import com.normalnywork.tundramarket.ui.kit.icons.Checkmark
import com.normalnywork.tundramarket.ui.kit.icons.ChevronRight
import com.normalnywork.tundramarket.ui.kit.icons.Comment
import com.normalnywork.tundramarket.ui.kit.icons.IllustrationDroneTent
import com.normalnywork.tundramarket.ui.kit.icons.Latitude
import com.normalnywork.tundramarket.ui.kit.icons.Longitude
import com.normalnywork.tundramarket.ui.kit.icons.Processing
import com.normalnywork.tundramarket.ui.kit.icons.Products
import com.normalnywork.tundramarket.ui.kit.icons.Rejected
import com.normalnywork.tundramarket.ui.kit.icons.Sent
import com.normalnywork.tundramarket.ui.kit.icons.Shop
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.Waiting
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import com.normalnywork.tundramarket.ui.screens.nomad.NomadMainComponent.CurrentOrderState
import com.normalnywork.tundramarket.ui.screens.nomad.NomadMainComponent.OrderNetworkState
import com.normalnywork.tundramarket.ui.tools.toDisplayCoordinate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun NomadMainContent(component: NomadMainComponent) {
    val currentOrderState by component.currentOrderState.collectAsState()

    NomadMainContent(
        currentOrderState = currentOrderState,
        onOpenHistoryClick = component::onOpenHistoryClicked,
        onCreateOrderClick = component::onCreateOrderClicked,
        onRepeatOrderClick = component::onRepeatOrderClicked,
        onCancelOrderClick = component::onCancelOrderClicked,
        onCreateOrderViaSmsClick = component::onCreateOrderViaSmsClicked,
    )
}

@Composable
private fun NomadMainContent(
    currentOrderState: CurrentOrderState,
    onOpenHistoryClick: () -> Unit,
    onCreateOrderClick: () -> Unit,
    onRepeatOrderClick: (CurrentOrderState.Order) -> Unit,
    onCancelOrderClick: (CurrentOrderState.Order) -> Unit,
    onCreateOrderViaSmsClick: (CurrentOrderState.Order) -> Unit,
) {
    val colors = LocalTMColors.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Column {
                TMCompactTopBar(title = stringResource(R.string.nomad_main_title))

                (currentOrderState as? CurrentOrderState.Order)
                    ?.let {
                        CurrentOrderOverviewCard(order = it)
                    }
            }
        },
        content = { paddings ->
            NomadMainMiddleContent(
                scrollState = scrollState,
                currentOrderState = currentOrderState,
                onCreateOrderClick = onCreateOrderClick,
                onRepeatOrderClick = onRepeatOrderClick,
                onCancelOrderClick = onCancelOrderClick,
                onCreateOrderViaSmsClick = onCreateOrderViaSmsClick,
                modifier = Modifier.padding(paddings),
            )
        },
        bottomBar = {
            val order = currentOrderState as? CurrentOrderState.Order
            NomadMainBottomCard(
                showDivider = scrollState.canScrollForward,
                showCreateOrderButton = order?.sourceOrder?.status?.isTerminal == true,
                onOpenHistoryClick = onOpenHistoryClick,
                onCreateOrderClick = onCreateOrderClick,
            )
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
    )
}

@Composable
private fun NomadMainMiddleContent(
    scrollState: ScrollState,
    currentOrderState: CurrentOrderState,
    onCreateOrderClick: () -> Unit,
    onRepeatOrderClick: (CurrentOrderState.Order) -> Unit,
    onCancelOrderClick: (CurrentOrderState.Order) -> Unit,
    onCreateOrderViaSmsClick: (CurrentOrderState.Order) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (currentOrderState is CurrentOrderState.Empty) {
            Arrangement.Center
        } else {
            Arrangement.Top
        },
    ) {
        when (currentOrderState) {
            CurrentOrderState.Empty -> EmptyOrderContent(
                onCreateOrderClick = onCreateOrderClick,
            )

            is CurrentOrderState.Order -> {
                AnimatedNetworkStatusCard(
                    networkState = currentOrderState.networkState,
                    onCreateOrderViaSmsClick = { onCreateOrderViaSmsClick(currentOrderState) },
                )
                OrderStatusCard(
                    order = currentOrderState,
                    onRepeatOrderClick = { onRepeatOrderClick(currentOrderState) },
                    onCancelOrderClick = { onCancelOrderClick(currentOrderState) },
                )
                Spacer(modifier = Modifier.height(16.dp))
                OrderStatusHistoryCard(history = currentOrderState.sourceOrder.statusHistory)
                Spacer(modifier = Modifier.height(16.dp))
                OrderDetailsCard(order = currentOrderState)
            }
        }
    }
}

@Composable
private fun AnimatedNetworkStatusCard(
    networkState: OrderNetworkState?,
    onCreateOrderViaSmsClick: () -> Unit,
) {
    var visibleNetworkState by remember { mutableStateOf(networkState) }

    LaunchedEffect(networkState) {
        if (networkState != null) {
            visibleNetworkState = networkState
        }
    }

    AnimatedVisibility(
        visible = networkState != null,
        enter = fadeIn() + expandVertically() + slideInVertically { it / 2 },
        exit = fadeOut() + shrinkVertically() + slideOutVertically { -it / 2 },
    ) {
        visibleNetworkState?.let { state ->
            Column {
                NetworkStatusCard(
                    networkState = state,
                    onCreateOrderViaSmsClick = onCreateOrderViaSmsClick,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun NetworkStatusCard(
    networkState: OrderNetworkState,
    onCreateOrderViaSmsClick: () -> Unit,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colors.secondaryVariant,
                shape = TMShapes.Medium,
            )
            .border(
                width = 1.dp,
                color = colors.secondary,
                shape = TMShapes.Medium,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        AnimatedContent(
            targetState = networkState.toNetworkStatusCardContent(),
            transitionSpec = {
                (fadeIn() + slideInVertically { it / 3 })
                    .togetherWith(fadeOut() + slideOutVertically { -it / 3 })
                    .using(SizeTransform(clip = false))
            },
            label = "NomadOrderNetworkStatus",
        ) { content ->
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NetworkStatusIcon(type = content.iconType)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = content.title.uppercase(),
                            style = typography.label,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = content.body,
                            style = typography.body,
                            color = colors.textSecondary,
                        )
                    }
                }

                if (content.action != NetworkStatusCardAction.None) {
                    TMButtonSlider(
                        text = stringResource(
                            when (content.action) {
                                NetworkStatusCardAction.SendSms ->
                                    R.string.nomad_main_network_status_send_sms_action
                                NetworkStatusCardAction.RetrySms ->
                                    R.string.nomad_main_network_status_retry_action
                            }
                        ),
                        onClick = onCreateOrderViaSmsClick,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NetworkStatusIcon(type: NetworkStatusCardIcon) {
    val colors = LocalTMColors.current

    when (type) {
        NetworkStatusCardIcon.Loading -> LoadingIndicator(
            modifier = Modifier.size(24.dp),
            color = colors.primary,
        )
        NetworkStatusCardIcon.BadConnection -> Icon(
            imageVector = TMIcons.BadConnection,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun EmptyOrderContent(onCreateOrderClick: () -> Unit) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                imageVector = TMIcons.IllustrationDroneTent,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(160.dp),
            )
            Text(
                text = stringResource(R.string.nomad_main_order_status_empty),
                style = typography.body,
                color = colors.textPrimary,
            )
        }
        TMButtonPrimary(
            text = stringResource(R.string.nomad_main_create_order_action),
            onClick = onCreateOrderClick,
        )
    }
}

@Composable
private fun OrderStatusCard(
    order: CurrentOrderState.Order,
    onRepeatOrderClick: () -> Unit,
    onCancelOrderClick: () -> Unit,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colors.backgroundCard,
                shape = TMShapes.Medium,
            )
            .border(
                width = 1.dp,
                color = colors.stroke,
                shape = TMShapes.Medium,
            )
            .padding(8.dp),
    ) {
        AnimatedContent(
            targetState = order.sourceOrder.status,
            transitionSpec = {
                (fadeIn() + slideInVertically { it / 3 })
                    .togetherWith(fadeOut() + slideOutVertically { -it / 3 })
            },
            label = "NomadOrderStatus",
        ) { status ->
            val content = status.toStatusCardContent()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = content.icon,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp),
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.nomad_main_status_card_title).uppercase(),
                        style = typography.captionLabel,
                        color = colors.textSecondary,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = content.title.uppercase(),
                            style = typography.label,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = content.body,
                            style = typography.body,
                            color = colors.textPrimary,
                        )
                    }
                }
            }
        }

        AnimatedContent(
            targetState = order.sourceOrder.status.toStatusCardAction(),
            transitionSpec = {
                (fadeIn() + slideInVertically { it / 2 })
                    .togetherWith(fadeOut() + slideOutVertically { it / 2 })
                    .using(SizeTransform(clip = false))
            },
            label = "NomadOrderStatusAction",
        ) {
            when (it) {
                StatusCardAction.None -> Box(modifier = Modifier.fillMaxWidth())
                StatusCardAction.Cancel -> TMButtonTertiary(
                    text = stringResource(R.string.nomad_main_cancel_order_action),
                    onClick = onCancelOrderClick,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                )
                StatusCardAction.Denied -> Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OrderStatusReasonCard(
                        text = stringResource(R.string.nomad_main_status_denied_reason_unavailable),
                    )
                    TMButtonSlider(
                        text = stringResource(R.string.nomad_main_repeat_order_action),
                        onClick = onRepeatOrderClick,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderStatusReasonCard(text: String) {
    Text(
        text = text,
        style = LocalTMTypography.current.body,
        color = LocalTMColors.current.textPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = LocalTMColors.current.background,
                shape = TMShapes.Small,
            )
            .border(
                width = 1.dp,
                color = LocalTMColors.current.stroke,
                shape = TMShapes.Small,
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@Composable
private fun OrderStatusHistoryCard(history: List<OrderStatusHistory>) {
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
private fun NomadMainBottomCard(
    showDivider: Boolean,
    showCreateOrderButton: Boolean,
    onOpenHistoryClick: () -> Unit,
    onCreateOrderClick: () -> Unit,
) {
    val colors = LocalTMColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background)
            .navigationBarsPadding()
            .animateContentSize(),
    ) {
        if (showDivider) HorizontalDivider(color = colors.stroke)

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(TMShapes.Medium)
                    .background(colors.backgroundCard)
                    .clickable(
                        role = Role.Button,
                        onClick = onOpenHistoryClick,
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = TMIcons.Waiting,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.nomad_main_history_action).uppercase(),
                        style = LocalTMTypography.current.label,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = stringResource(R.string.nomad_main_history_subtitle),
                        style = LocalTMTypography.current.bodySmall,
                        color = colors.textSecondary,
                    )
                }
                Icon(
                    imageVector = TMIcons.ChevronRight,
                    contentDescription = null,
                    tint = colors.textPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }

            AnimatedVisibility(
                visible = showCreateOrderButton,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 },
            ) {
                TMButtonPrimary(
                    text = stringResource(R.string.nomad_main_make_order_action),
                    onClick = onCreateOrderClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun OrderStatus.toStatusCardContent(): StatusCardContent =
    when (this) {
        OrderStatus.Created -> StatusCardContent(
            title = stringResource(R.string.nomad_main_status_created_title),
            body = stringResource(R.string.nomad_main_status_created_body),
            icon = TMIcons.Waiting,
        )

        OrderStatus.Processing -> StatusCardContent(
            title = stringResource(R.string.nomad_main_status_processing_title),
            body = stringResource(R.string.nomad_main_status_processing_body),
            icon = TMIcons.Processing,
        )

        OrderStatus.Sent -> StatusCardContent(
            title = stringResource(R.string.nomad_main_status_sent_title),
            body = stringResource(R.string.nomad_main_status_sent_body),
            icon = TMIcons.Processing,
        )

        OrderStatus.Completed -> StatusCardContent(
            title = stringResource(R.string.nomad_main_status_completed_title),
            body = stringResource(R.string.nomad_main_status_completed_body),
            icon = TMIcons.Checkmark,
        )

        OrderStatus.Cancelled -> StatusCardContent(
            title = stringResource(R.string.nomad_main_status_cancelled_title),
            body = stringResource(R.string.nomad_main_status_cancelled_body),
            icon = TMIcons.Rejected,
        )

        OrderStatus.Denied -> StatusCardContent(
            title = stringResource(R.string.nomad_main_status_denied_title),
            body = stringResource(R.string.nomad_main_status_denied_body),
            icon = TMIcons.Rejected,
        )
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

private fun OrderStatus.toStatusCardAction(): StatusCardAction =
    when (this) {
        OrderStatus.Created -> StatusCardAction.Cancel
        OrderStatus.Denied -> StatusCardAction.Denied
        OrderStatus.Processing,
        OrderStatus.Sent,
        OrderStatus.Completed,
        OrderStatus.Cancelled -> StatusCardAction.None
    }

@Composable
private fun OrderNetworkState.toNetworkStatusCardContent(): NetworkStatusCardContent =
    when (this) {
        is OrderNetworkState.Create -> status.toNetworkStatusCardContent()
        OrderNetworkState.Updating -> NetworkStatusCardContent(
            title = stringResource(R.string.nomad_main_network_status_update_loading_title),
            body = stringResource(R.string.nomad_main_network_status_update_loading_body),
            iconType = NetworkStatusCardIcon.Loading,
            action = NetworkStatusCardAction.None,
        )
        OrderNetworkState.UpdateFailed -> NetworkStatusCardContent(
            title = stringResource(R.string.nomad_main_network_status_waiting_title),
            body = stringResource(R.string.nomad_main_network_status_update_failed_body),
            iconType = NetworkStatusCardIcon.BadConnection,
            action = NetworkStatusCardAction.None,
        )
    }

@Composable
private fun OrderNetworkStatus.toNetworkStatusCardContent(): NetworkStatusCardContent =
    when (this) {
        OrderNetworkStatus.Enqueued,
        OrderNetworkStatus.Processing,
        OrderNetworkStatus.Loading -> NetworkStatusCardContent(
            title = stringResource(R.string.nomad_main_network_status_loading_title),
            body = stringResource(R.string.nomad_main_network_status_create_loading_body),
            iconType = NetworkStatusCardIcon.Loading,
            action = NetworkStatusCardAction.None,
        )

        OrderNetworkStatus.Failed -> NetworkStatusCardContent(
            title = stringResource(R.string.nomad_main_network_status_waiting_title),
            body = stringResource(R.string.nomad_main_network_status_create_failed_body),
            iconType = NetworkStatusCardIcon.BadConnection,
            action = NetworkStatusCardAction.SendSms,
        )

        OrderNetworkStatus.LoadingSms -> NetworkStatusCardContent(
            title = stringResource(R.string.nomad_main_network_status_loading_title),
            body = stringResource(R.string.nomad_main_network_status_sms_loading_body),
            iconType = NetworkStatusCardIcon.Loading,
            action = NetworkStatusCardAction.None,
        )

        OrderNetworkStatus.SmsFailed -> NetworkStatusCardContent(
            title = stringResource(R.string.nomad_main_network_status_waiting_title),
            body = stringResource(R.string.nomad_main_network_status_sms_failed_body),
            iconType = NetworkStatusCardIcon.BadConnection,
            action = NetworkStatusCardAction.RetrySms,
        )
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

@Composable
private fun Long.toStatusHistoryTime(): String {
    val calendar = Calendar.getInstance()
    val itemCalendar = Calendar.getInstance().apply {
        timeInMillis = this@toStatusHistoryTime
    }
    val isToday = calendar.get(Calendar.YEAR) == itemCalendar.get(Calendar.YEAR) &&
        calendar.get(Calendar.DAY_OF_YEAR) == itemCalendar.get(Calendar.DAY_OF_YEAR)
    val pattern = if (isToday) TODAY_TIME_PATTERN else DATE_TIME_PATTERN
    val formattedTime = SimpleDateFormat(pattern, LocalLocale.current.platformLocale)
        .format(Date(this))

    return if (isToday) {
        stringResource(R.string.nomad_main_status_history_today_time, formattedTime)
    } else {
        formattedTime
    }
}

private val OrderStatus.isTerminal: Boolean
    get() = this == OrderStatus.Completed ||
        this == OrderStatus.Denied ||
        this == OrderStatus.Cancelled

private data class StatusCardContent(
    val title: String,
    val body: String,
    val icon: ImageVector,
)

private data class NetworkStatusCardContent(
    val title: String,
    val body: String,
    val iconType: NetworkStatusCardIcon,
    val action: NetworkStatusCardAction,
)

private data class StatusHistoryIconColors(
    val backgroundColor: Color,
    val contentColor: Color,
)

private enum class StatusCardAction {

    None,
    Cancel,
    Denied,
}

private enum class NetworkStatusCardIcon {

    Loading,
    BadConnection,
}

private enum class NetworkStatusCardAction {

    None,
    SendSms,
    RetrySms,
}

private object StatusHistoryTokens {

    val IconContainerSize = 40.dp
    val IconSize = 24.dp
    val DividerHeight = 12.dp
    val DividerWidth = 2.dp
    val DashLength = 2.dp
    val DashGap = 2.dp
}

private const val TODAY_TIME_PATTERN = "H:mm"
private const val DATE_TIME_PATTERN = "dd.MM.yyyy, H:mm"

@Composable
private fun OrderDetailsCard(order: CurrentOrderState.Order) {
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
                order.products.forEach { product ->
                    ProductRow(product = product)
                }
            }
        }
        OrderDetailsSection(
            title = stringResource(R.string.nomad_create_order_overview_comment_section),
            icon = TMIcons.Comment,
        ) {
            val comment = order.sourceOrder.comment

            OrderDetailsSurface {
                OrderDetailsText(
                    text = comment.ifEmpty { stringResource(R.string.nomad_create_order_overview_no_comment) },
                    colorSecondary = comment.isEmpty(),
                )
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
private fun ProductRow(product: NomadMainComponent.ProductItem) {
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
            text = stringResource(R.string.nomad_create_order_overview_product_quantity, product.quantity),
            colorSecondary = true,
        )
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

@Composable
private fun CurrentOrderOverviewCard(order: CurrentOrderState.Order) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = colors.stroke,
                shape = TMShapes.Medium,
            )
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.nomad_main_order_title, order.id).uppercase(),
            style = typography.label,
            color = colors.textPrimary,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OrderOverviewInfo(
                icon = TMIcons.Shop,
                text = order.sourceOrder.tradingStation.name,
                modifier = Modifier.weight(1f, fill = false),
            )
            OrderOverviewInfo(
                icon = TMIcons.Latitude,
                text = order.sourceOrder.location.latitude.toDisplayCoordinate(),
            )
            OrderOverviewInfo(
                icon = TMIcons.Longitude,
                text = order.sourceOrder.location.longitude.toDisplayCoordinate(),
            )
        }
    }
}

@Composable
private fun OrderOverviewInfo(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTMColors.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = text,
            style = LocalTMTypography.current.bodySmall,
            color = colors.textSecondary,
            maxLines = 1,
        )
    }
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun NomadMainContentPreview() {
    NomadMainContent(
        currentOrderState = CurrentOrderState.Empty,
        onOpenHistoryClick = {},
        onCreateOrderClick = {},
        onRepeatOrderClick = {},
        onCancelOrderClick = {},
        onCreateOrderViaSmsClick = {},
    )
}
