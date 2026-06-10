package com.normalnywork.tundramarket.ui.screens.tradingstation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.domain.entities.Location
import com.normalnywork.tundramarket.domain.entities.Order
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatusHistory
import com.normalnywork.tundramarket.domain.entities.Product
import com.normalnywork.tundramarket.domain.entities.TradingStation
import com.normalnywork.tundramarket.domain.entities.denialComment
import com.normalnywork.tundramarket.ui.kit.components.TMAlertDialog
import com.normalnywork.tundramarket.ui.kit.components.TMAlertDialogTextButton
import com.normalnywork.tundramarket.ui.kit.components.TMButtonSecondary
import com.normalnywork.tundramarket.ui.kit.components.TMButtonSlider
import com.normalnywork.tundramarket.ui.kit.components.TMTextField
import com.normalnywork.tundramarket.ui.kit.components.TMTopBar
import com.normalnywork.tundramarket.ui.kit.icons.Checkmark
import com.normalnywork.tundramarket.ui.kit.icons.Comment
import com.normalnywork.tundramarket.ui.kit.icons.Distance
import com.normalnywork.tundramarket.ui.kit.icons.Latitude
import com.normalnywork.tundramarket.ui.kit.icons.Location
import com.normalnywork.tundramarket.ui.kit.icons.Longitude
import com.normalnywork.tundramarket.ui.kit.icons.Processing
import com.normalnywork.tundramarket.ui.kit.icons.Products
import com.normalnywork.tundramarket.ui.kit.icons.Rejected
import com.normalnywork.tundramarket.ui.kit.icons.Sent
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import com.normalnywork.tundramarket.ui.shared.OrderStatusHistoryCard
import com.normalnywork.tundramarket.ui.tools.toDisplayCoordinate
import com.normalnywork.tundramarket.utils.CoordinateDistanceCalculator.distanceTo
import kotlin.math.roundToInt

@Composable
fun TradingStationOrderDetailsContent(component: TradingStationOrderDetailsComponent) {
    val order by component.order.collectAsState()
    val isDeclineDialogVisible by component.isDeclineDialogVisible.collectAsState()
    val isIncompleteSendDialogVisible by component.isIncompleteSendDialogVisible.collectAsState()

    TradingStationOrderDetailsContent(
        order = order,
        declineComment = component.declineComment,
        isDeclineDialogVisible = isDeclineDialogVisible,
        isIncompleteSendDialogVisible = isIncompleteSendDialogVisible,
        onBackClick = component::onBackClicked,
        onAcceptOrderClick = component::onAcceptOrderClicked,
        onDeclineOrderClick = component::onDeclineOrderClicked,
        onDeclineDialogDismiss = component::onDeclineDialogDismissed,
        onDeclineOrderConfirm = component::onDeclineOrderConfirmed,
        onOrderProductAssembledChange = component::onOrderProductAssembledChanged,
        onSendOrderClick = component::onSendOrderClicked,
        onIncompleteSendDialogDismiss = component::onIncompleteSendDialogDismissed,
        onIncompleteSendConfirm = component::onIncompleteSendConfirmed,
        onDeliveryConfirm = component::onDeliveryConfirmed,
    )
}

@Composable
private fun TradingStationOrderDetailsContent(
    order: Order,
    declineComment: TextFieldState,
    isDeclineDialogVisible: Boolean,
    isIncompleteSendDialogVisible: Boolean,
    onBackClick: () -> Unit,
    onAcceptOrderClick: () -> Unit,
    onDeclineOrderClick: () -> Unit,
    onDeclineDialogDismiss: () -> Unit,
    onDeclineOrderConfirm: () -> Unit,
    onOrderProductAssembledChange: (Int, Boolean) -> Unit,
    onSendOrderClick: () -> Unit,
    onIncompleteSendDialogDismiss: () -> Unit,
    onIncompleteSendConfirm: () -> Unit,
    onDeliveryConfirm: () -> Unit,
) {
    val colors = LocalTMColors.current
    val scrollState = rememberSaveable(saver = ScrollState.Saver) { ScrollState(0) }
    val hasBottomBar = order.status == OrderStatus.Created || order.status == OrderStatus.Processing

    Scaffold(
        topBar = {
            TMTopBar(
                title = stringResource(R.string.trading_station_order_details_title, order.id),
                onBack = onBackClick,
                showDivider = scrollState.canScrollBackward,
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = hasBottomBar,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
            ) {
                TradingStationOrderDetailsBottomBar(
                    orderStatus = order.status,
                    showDivider = scrollState.canScrollForward,
                    onAcceptOrderClick = onAcceptOrderClick,
                    onDeclineOrderClick = onDeclineOrderClick,
                    onSendOrderClick = onSendOrderClick,
                )
            }
        },
        containerColor = colors.background,
        modifier = Modifier.fillMaxSize(),
    ) { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddings)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TradingStationOrderDetailsCards(
                order = order,
                onOrderProductAssembledChange = onOrderProductAssembledChange,
                onDeliveryConfirm = onDeliveryConfirm,
            )
        }
    }

    if (isDeclineDialogVisible) {
        DeclineOrderDialog(
            comment = declineComment,
            onDismiss = onDeclineDialogDismiss,
            onConfirm = onDeclineOrderConfirm,
        )
    }

    if (isIncompleteSendDialogVisible) {
        IncompleteSendDialog(
            onDismiss = onIncompleteSendDialogDismiss,
            onConfirm = onIncompleteSendConfirm,
        )
    }
}

@Composable
private fun TradingStationOrderDetailsCards(
    order: Order,
    onOrderProductAssembledChange: (Int, Boolean) -> Unit,
    onDeliveryConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        TradingStationOrderStatusCard(
            orderStatus = order.status,
            action = if (order.status == OrderStatus.Sent) {{
                TMButtonSlider(
                    text = stringResource(R.string.trading_station_order_details_confirm_action),
                    onClick = onDeliveryConfirm,
                    modifier = Modifier.fillMaxWidth(),
                )
            }} else null,
            comment = order.statusHistory.denialComment,
        )
        AnimatedVisibility(visible = order.status == OrderStatus.Processing) {
            OrderAssemblyCard(
                order = order,
                onOrderProductAssembledChange = onOrderProductAssembledChange,
            )
        }
        Spacer(Modifier.height(16.dp))
        TradingStationOrderDetailsCard(order = order)
        Spacer(Modifier.height(16.dp))
        OrderStatusHistoryCard(history = order.statusHistory)
    }
}

@Composable
private fun TradingStationOrderDetailsBottomBar(
    orderStatus: OrderStatus,
    showDivider: Boolean,
    onAcceptOrderClick: () -> Unit,
    onDeclineOrderClick: () -> Unit,
    onSendOrderClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
            color = if (showDivider) {
                LocalTMColors.current.stroke
            } else {
                LocalTMColors.current.background
            },
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalTMColors.current.background)
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when (orderStatus) {
                OrderStatus.Created -> {
                    TMButtonSlider(
                        text = stringResource(R.string.trading_station_order_details_accept_action),
                        onClick = onAcceptOrderClick,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    TMButtonSecondary(
                        text = stringResource(R.string.trading_station_order_details_decline_action),
                        onClick = onDeclineOrderClick,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                OrderStatus.Processing -> TMButtonSlider(
                    text = stringResource(R.string.trading_station_order_details_send_action),
                    onClick = onSendOrderClick,
                    modifier = Modifier.fillMaxWidth(),
                )
                OrderStatus.Sent,
                OrderStatus.Completed,
                OrderStatus.Cancelled,
                OrderStatus.Denied,
                -> Unit
            }
        }
    }
}

@Composable
private fun TradingStationOrderStatusCard(
    orderStatus: OrderStatus,
    comment: String? = null,
    action: (@Composable () -> Unit)? = null,
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
            targetState = orderStatus,
            transitionSpec = {
                (fadeIn() + slideInVertically { it / 3 })
                    .togetherWith(fadeOut() + slideOutVertically { -it / 3 })
            },
            label = "OrderStatus",
        ) { status ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = status.detailsIcon(),
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
                            text = stringResource(status.detailsTitleRes()).uppercase(),
                            style = typography.label,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = stringResource(status.detailsBodyRes()),
                            style = typography.body,
                            color = colors.textPrimary,
                        )
                    }
                }
            }
        }

        AnimatedContent(
            targetState = orderStatus,
            transitionSpec = {
                (fadeIn() + slideInVertically { it / 2 })
                    .togetherWith(fadeOut() + slideOutVertically { it / 2 })
                    .using(SizeTransform(clip = false))
            },
            label = "NomadOrderStatusAction",
        ) {
            if (it == OrderStatus.Denied) {
                Text(
                    text = comment ?: stringResource(R.string.nomad_main_status_denied_reason_unavailable),
                    style = LocalTMTypography.current.body,
                    color = LocalTMColors.current.textPrimary,
                    modifier = Modifier
                        .padding(top = 16.dp)
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
            } else {
                Spacer(Modifier.fillMaxWidth())
            }
        }

        AnimatedContent(
            targetState = action,
            transitionSpec = {
                (fadeIn() + slideInVertically { it / 2 })
                    .togetherWith(fadeOut() + slideOutVertically { it / 2 })
                    .using(SizeTransform(clip = false))
            },
            label = "OrderStatusAction",
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (it == null) 0.dp else 16.dp)
            ) {
                it?.invoke()
            }
        }
    }
}

@Composable
private fun OrderAssemblyCard(
    order: Order,
    onOrderProductAssembledChange: (Int, Boolean) -> Unit,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current
    val assembledCount = order.assembledProductIds.size
    val totalCount = order.cart.size

    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = colors.stroke,
                shape = TMShapes.Medium,
            )
            .animateContentSize()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.trading_station_order_assembly_title).uppercase(),
                style = typography.title,
                color = colors.textPrimary,
            )
            Text(
                text = stringResource(R.string.trading_station_order_assembly_count, assembledCount, totalCount).uppercase(),
                style = typography.captionLabel,
                color = colors.textSecondary,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            order.cart.forEachIndexed { index, (product, quantity) ->
                val isAssembled = product.id in order.assembledProductIds

                AssemblyProductRow(
                    product = product,
                    quantity = quantity,
                    isAssembled = isAssembled,
                    onAssembledChange = { onOrderProductAssembledChange(product.id, !isAssembled) },
                )
                if (index < order.cart.lastIndex) {
                    HorizontalDivider(
                        color = colors.stroke,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun AssemblyProductRow(
    product: Product,
    quantity: Int,
    isAssembled: Boolean,
    onAssembledChange: () -> Unit,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TMShapes.Small)
            .clickable(
                role = Role.Checkbox,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = colors.primary),
                onClick = onAssembledChange,
            )
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = product.name,
                style = typography.subtitle,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(R.string.nomad_create_order_overview_product_quantity, quantity),
                style = typography.bodySmall,
                color = colors.textSecondary,
            )
        }
        Checkbox(
            checked = isAssembled,
            onCheckedChange = { onAssembledChange() },
            colors = CheckboxDefaults.colors(
                checkedColor = colors.primary,
                checkmarkColor = colors.onPrimary,
                uncheckedColor = colors.textSecondary,
            )
        )
    }
}

@Composable
private fun AssemblyCheckbox(checked: Boolean) {
    val colors = LocalTMColors.current
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) colors.primary else Color.Transparent,
        label = "AssemblyCheckboxBackground",
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) colors.primary else colors.textSecondary,
        label = "AssemblyCheckboxBorder",
    )

    Box(
        modifier = Modifier
            .size(22.dp)
            .background(
                color = backgroundColor,
                shape = TMShapes.Small,
            )
            .border(
                width = 2.dp,
                color = borderColor,
                shape = TMShapes.Small,
            ),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
        ) {
            Icon(
                imageVector = TMIcons.Checkmark,
                contentDescription = null,
                tint = colors.onPrimary,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun TradingStationOrderDetailsCard(order: Order) {
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
            title = stringResource(R.string.nomad_create_order_overview_location_section),
            icon = TMIcons.Location,
        ) {
            OrderDetailsSurface {
                OrderDetailsIconTextRow(
                    icon = TMIcons.Latitude,
                    text = order.location.latitude.toDisplayCoordinate(),
                )
                OrderDetailsIconTextRow(
                    icon = TMIcons.Longitude,
                    text = order.location.longitude.toDisplayCoordinate(),
                )
                OrderDetailsIconTextRow(
                    icon = TMIcons.Distance,
                    text = stringResource(
                        R.string.trading_station_order_details_distance_from_station,
                        (order.tradingStation.location distanceTo order.location).roundToInt(),
                    ),
                )
            }
        }
        OrderDetailsSection(
            title = stringResource(R.string.nomad_create_order_overview_products_section),
            icon = TMIcons.Products,
        ) {
            OrderDetailsSurface {
                order.cart.forEach { (product, quantity) ->
                    ProductRow(
                        product = product,
                        quantity = quantity,
                    )
                }
                HorizontalDivider(color = colors.stroke)
                OrderTotalsRow(cart = order.cart)
            }
        }
        OrderDetailsSection(
            title = stringResource(R.string.nomad_create_order_overview_comment_section),
            icon = TMIcons.Comment,
        ) {
            OrderDetailsSurface {
                OrderDetailsText(
                    text = order.comment.ifEmpty { stringResource(R.string.nomad_create_order_overview_no_comment) },
                    colorSecondary = order.comment.isEmpty(),
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
        OrderDetailsSecondaryText(
            text = stringResource(R.string.nomad_create_order_overview_product_quantity, quantity),
        )
    }
}

@Composable
private fun OrderTotalsRow(cart: List<Pair<Product, Int>>) {
    val totalVolume = cart.sumOf { (product, quantity) -> product.volume.toDouble() * quantity }.toFloat()
    val totalWeight = cart.sumOf { (product, quantity) -> product.weight.toDouble() * quantity }.toFloat()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OrderTotalItem(
            title = stringResource(R.string.trading_station_order_details_volume_label),
            value = stringResource(
                R.string.nomad_create_order_product_volume,
                totalVolume.toMetricText(),
            ),
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .height(16.dp)
                .width(1.dp)
                .background(LocalTMColors.current.stroke),
        )
        OrderTotalItem(
            title = stringResource(R.string.trading_station_order_details_weight_label),
            value = stringResource(
                R.string.nomad_create_order_product_weight,
                totalWeight.toMetricText(),
            ),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun OrderTotalItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OrderDetailsText(
            text = title,
            modifier = Modifier.weight(1f),
        )
        OrderDetailsSecondaryText(text = value)
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
private fun OrderDetailsSecondaryText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTMColors.current

    Text(
        text = text,
        style = LocalTMTypography.current.bodySmall,
        color = colors.textSecondary,
        modifier = modifier,
    )
}

@Composable
private fun DeclineOrderDialog(
    comment: TextFieldState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    TMAlertDialog(
        title = stringResource(R.string.trading_station_order_decline_dialog_title),
        body = stringResource(R.string.trading_station_order_decline_dialog_body),
        onDismiss = onDismiss,
        confirmAction = {
            TMAlertDialogTextButton(
                text = stringResource(R.string.trading_station_order_details_decline_action),
                enabled = comment.text.isNotBlank(),
                onClick = onConfirm,
            )
        },
    ) {
        val focusRequester = remember { FocusRequester() }

        TMTextField(
            state = comment,
            multiline = true,
            height = 84.dp,
            modifier = Modifier.focusRequester(focusRequester)
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun IncompleteSendDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    TMAlertDialog(
        title = stringResource(R.string.trading_station_order_incomplete_send_dialog_title),
        body = stringResource(R.string.trading_station_order_incomplete_send_dialog_body),
        onDismiss = onDismiss,
        confirmAction = {
            TMAlertDialogTextButton(
                text = stringResource(R.string.trading_station_order_details_send_action),
                onClick = onConfirm,
            )
        },
    )
}

private fun OrderStatus.detailsTitleRes() = when (this) {
    OrderStatus.Created -> R.string.trading_station_order_status_created_title
    OrderStatus.Processing -> R.string.trading_station_order_status_processing_title
    OrderStatus.Sent -> R.string.trading_station_order_status_sent_title
    OrderStatus.Completed -> R.string.trading_station_order_status_completed_title
    OrderStatus.Cancelled -> R.string.trading_station_order_status_cancelled_title
    OrderStatus.Denied -> R.string.trading_station_order_status_denied_title
}

private fun OrderStatus.detailsBodyRes() = when (this) {
    OrderStatus.Created -> R.string.trading_station_order_details_status_created_body
    OrderStatus.Processing -> R.string.trading_station_order_details_status_processing_body
    OrderStatus.Sent -> R.string.trading_station_order_details_status_sent_body
    OrderStatus.Completed -> R.string.trading_station_order_status_completed_body
    OrderStatus.Cancelled -> R.string.trading_station_order_status_cancelled_body
    OrderStatus.Denied -> R.string.trading_station_order_status_denied_body
}

private fun OrderStatus.detailsIcon() = when (this) {
    OrderStatus.Created -> TMIcons.Processing
    OrderStatus.Processing -> TMIcons.Processing
    OrderStatus.Sent -> TMIcons.Sent
    OrderStatus.Completed -> TMIcons.Checkmark
    OrderStatus.Cancelled,
    OrderStatus.Denied,
    -> TMIcons.Rejected
}

private fun Float.toMetricText(): String {
    val normalized = this / 1000f
    return if (normalized % 1f == 0f) {
        normalized.roundToInt().toString()
    } else {
        "%.1f".format(normalized).replace('.', ',')
    }
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun PreviewNewOrderDetails() {
    TradingStationOrderDetailsContent(
        order = sampleTradingStationDetailsOrder(status = OrderStatus.Created),
        declineComment = TextFieldState(),
        isDeclineDialogVisible = false,
        isIncompleteSendDialogVisible = false,
        onBackClick = {},
        onAcceptOrderClick = {},
        onDeclineOrderClick = {},
        onDeclineDialogDismiss = {},
        onDeclineOrderConfirm = {},
        onOrderProductAssembledChange = { _, _ -> },
        onSendOrderClick = {},
        onIncompleteSendDialogDismiss = {},
        onIncompleteSendConfirm = {},
        onDeliveryConfirm = {},
    )
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun PreviewProcessingOrderDetails() {
    TradingStationOrderDetailsContent(
        order = sampleTradingStationDetailsOrder(
            status = OrderStatus.Processing,
            assembledProductIds = setOf(1),
        ),
        declineComment = TextFieldState(),
        isDeclineDialogVisible = false,
        isIncompleteSendDialogVisible = true,
        onBackClick = {},
        onAcceptOrderClick = {},
        onDeclineOrderClick = {},
        onDeclineDialogDismiss = {},
        onDeclineOrderConfirm = {},
        onOrderProductAssembledChange = { _, _ -> },
        onSendOrderClick = {},
        onIncompleteSendDialogDismiss = {},
        onIncompleteSendConfirm = {},
        onDeliveryConfirm = {},
    )
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun PreviewSentOrderDetails() {
    TradingStationOrderDetailsContent(
        order = sampleTradingStationDetailsOrder(status = OrderStatus.Sent),
        declineComment = TextFieldState(),
        isDeclineDialogVisible = false,
        isIncompleteSendDialogVisible = false,
        onBackClick = {},
        onAcceptOrderClick = {},
        onDeclineOrderClick = {},
        onDeclineDialogDismiss = {},
        onDeclineOrderConfirm = {},
        onOrderProductAssembledChange = { _, _ -> },
        onSendOrderClick = {},
        onIncompleteSendDialogDismiss = {},
        onIncompleteSendConfirm = {},
        onDeliveryConfirm = {},
    )
}

private fun sampleTradingStationDetailsOrder(
    status: OrderStatus,
    assembledProductIds: Set<Int> = emptySet(),
): Order {
    val now = System.currentTimeMillis()
    val history = buildList {
        add(
            OrderStatusHistory(
                status = OrderStatus.Created,
                time = now - 3_600_000,
            ),
        )
        if (status != OrderStatus.Created) {
            add(
                OrderStatusHistory(
                    status = status,
                    time = now,
                ),
            )
        }
    }

    return Order(
        id = 172,
        nomadId = 11,
        tradingStation = TradingStation(
            id = 1,
            name = "Паюта",
            phone = null,
            location = Location(
                latitude = 67.984f,
                longitude = 68.586f,
            ),
        ),
        cart = listOf(
            Product(
                id = 1,
                name = "Хлеб",
                details = null,
                weight = 1_000f,
                volume = 2_000f,
            ) to 2,
            Product(
                id = 2,
                name = "Молоко",
                details = null,
                weight = 1_000f,
                volume = 1_000f,
            ) to 1,
            Product(
                id = 3,
                name = "Крупа",
                details = null,
                weight = 500f,
                volume = 1_000f,
            ) to 1,
        ),
        assembledProductIds = assembledProductIds,
        location = Location(
            latitude = 67.983f,
            longitude = 68.589f,
        ),
        comment = "",
        status = status,
        statusHistory = history,
    )
}
