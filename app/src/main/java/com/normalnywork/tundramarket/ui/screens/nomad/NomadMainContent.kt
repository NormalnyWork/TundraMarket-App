package com.normalnywork.tundramarket.ui.screens.nomad

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.domain.entities.OrderNetworkStatus
import com.normalnywork.tundramarket.domain.entities.OrderStatus
import com.normalnywork.tundramarket.domain.entities.denialComment
import com.normalnywork.tundramarket.domain.entities.isTerminal
import com.normalnywork.tundramarket.ui.kit.components.TMAlertDialog
import com.normalnywork.tundramarket.ui.kit.components.TMAlertDialogTextButton
import com.normalnywork.tundramarket.ui.kit.components.TMButtonPrimary
import com.normalnywork.tundramarket.ui.kit.components.TMButtonSlider
import com.normalnywork.tundramarket.ui.kit.components.TMButtonTertiary
import com.normalnywork.tundramarket.ui.kit.components.TMCompactTopBar
import com.normalnywork.tundramarket.ui.kit.components.TMTextField
import com.normalnywork.tundramarket.ui.kit.icons.BadConnection
import com.normalnywork.tundramarket.ui.kit.icons.Checkmark
import com.normalnywork.tundramarket.ui.kit.icons.ChevronRight
import com.normalnywork.tundramarket.ui.kit.icons.IllustrationDroneTent
import com.normalnywork.tundramarket.ui.kit.icons.LatitudeFilled
import com.normalnywork.tundramarket.ui.kit.icons.LongitudeFilled
import com.normalnywork.tundramarket.ui.kit.icons.Processing
import com.normalnywork.tundramarket.ui.kit.icons.Rejected
import com.normalnywork.tundramarket.ui.kit.icons.ShopFilled
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.Waiting
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import com.normalnywork.tundramarket.ui.screens.nomad.NomadMainComponent.CreateOrderViaSmsState
import com.normalnywork.tundramarket.ui.screens.nomad.NomadMainComponent.CurrentOrderState
import com.normalnywork.tundramarket.ui.shared.OrderDetailsCard
import com.normalnywork.tundramarket.ui.shared.OrderStatusHistoryCard
import com.normalnywork.tundramarket.ui.tools.SmsOrderCommentInputTransformation
import com.normalnywork.tundramarket.ui.tools.toDisplayCoordinate

@Composable
fun NomadMainContent(component: NomadMainComponent) {
    val currentOrderState by component.currentOrderState.collectAsState()
    val isCreateOrderViaSmsForbidden by component.isCreateOrderViaSmsForbidden.collectAsState()
    val activity = LocalActivity.current
    val context = LocalContext.current

    var showSmsPermissionDialog by rememberSaveable { mutableStateOf(false) }
    var isRetryingSmsPermission by rememberSaveable { mutableStateOf(false) }
    var smsCommentDialogState by remember { mutableStateOf<TextFieldState?>(null) }

    fun tryCreateOrderViaSms() {
        when (val state = component.getCreateOrderViaSmsState()) {
            is CreateOrderViaSmsState.Ready -> component.onCreateOrderViaSmsClicked(state.comment)
            is CreateOrderViaSmsState.CommentEditRequired -> {
                smsCommentDialogState = TextFieldState(initialText = state.comment)
            }
            CreateOrderViaSmsState.Unavailable -> Unit
        }
    }

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            tryCreateOrderViaSms()
            showSmsPermissionDialog = false
        } else if (isRetryingSmsPermission) {
            showSmsPermissionDialog = false
            component.onCreateOrderViaSmsForbidden()
        } else {
            showSmsPermissionDialog = true
        }

        isRetryingSmsPermission = false
    }

    fun requestCreateOrderViaSms() {
        if (
            isCreateOrderViaSmsForbidden ||
            (currentOrderState as? CurrentOrderState.Order)?.sourceOrder?.tradingStation?.phone == null
        ) return

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            tryCreateOrderViaSms()
        } else if (
            activity != null &&
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SEND_SMS)
        ) {
            showSmsPermissionDialog = true
        } else {
            isRetryingSmsPermission = false
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }

    NomadMainContent(
        currentOrderState = currentOrderState,
        isCreateOrderViaSmsForbidden = isCreateOrderViaSmsForbidden,
        onOpenHistoryClick = component::onOpenHistoryClicked,
        onCreateOrderClick = component::onCreateOrderClicked,
        onRepeatOrderClick = component::onRepeatOrderClicked,
        onCancelOrderClick = component::onCancelOrderClicked,
        onCreateOrderViaSmsClick = ::requestCreateOrderViaSms,
    )

    if (showSmsPermissionDialog) {
        TMAlertDialog(
            title = stringResource(R.string.nomad_main_sms_permission_dialog_title),
            body = stringResource(R.string.nomad_main_sms_permission_dialog_body),
            onDismiss = { showSmsPermissionDialog = false },
            confirmAction = {
                TMAlertDialogTextButton(
                    text = stringResource(R.string.nomad_main_sms_permission_dialog_confirm_action),
                    onClick = {
                        isRetryingSmsPermission = true
                        smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                    },
                )
            },
        )
    }

    smsCommentDialogState?.let { commentState ->
        val comment = commentState.text.toString().trim()
        val commentSmsState = component.getCreateOrderViaSmsCommentState(comment)
        val smsLength = commentSmsState.smsLength
        val canSendWithComment = commentSmsState.canSend
        val colors = LocalTMColors.current

        TMAlertDialog(
            title = stringResource(R.string.nomad_main_sms_comment_dialog_title),
            body = stringResource(R.string.nomad_main_sms_comment_dialog_body),
            onDismiss = { smsCommentDialogState = null },
            confirmAction = {
                TMAlertDialogTextButton(
                    text = stringResource(R.string.nomad_main_sms_comment_dialog_confirm_action),
                    enabled = canSendWithComment,
                    onClick = {
                        if (canSendWithComment) {
                            smsCommentDialogState = null
                            component.onCreateOrderViaSmsClicked(comment)
                        }
                    },
                )
            },
        ) {
            TMTextField(
                state = commentState,
                placeholder = stringResource(R.string.nomad_main_sms_comment_dialog_placeholder),
                multiline = true,
                height = 132.dp,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                inputTransformation = SmsOrderCommentInputTransformation,
            )
            Text(
                text = "${smsLength ?: 0}/${commentSmsState.smsLimit}",
                style = LocalTMTypography.current.caption,
                color = if (smsLength != null && smsLength > commentSmsState.smsLimit) {
                    colors.primary
                } else {
                    colors.textSecondary
                },
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

@Composable
private fun NomadMainContent(
    currentOrderState: CurrentOrderState,
    isCreateOrderViaSmsForbidden: Boolean,
    onOpenHistoryClick: () -> Unit,
    onCreateOrderClick: () -> Unit,
    onRepeatOrderClick: () -> Unit,
    onCancelOrderClick: () -> Unit,
    onCreateOrderViaSmsClick: () -> Unit,
) {
    val colors = LocalTMColors.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Column {
                TMCompactTopBar(title = stringResource(R.string.app_name))

                (currentOrderState as? CurrentOrderState.Order)
                    ?.let {
                        CurrentOrderOverviewCard(order = it)
                    }

                if (scrollState.canScrollBackward) HorizontalDivider(color = colors.stroke)
            }
        },
        content = { paddings ->
            NomadMainMiddleContent(
                scrollState = scrollState,
                currentOrderState = currentOrderState,
                onCreateOrderClick = onCreateOrderClick,
                onRepeatOrderClick = onRepeatOrderClick,
                onCancelOrderClick = onCancelOrderClick,
                isCreateOrderViaSmsForbidden = isCreateOrderViaSmsForbidden,
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
    onRepeatOrderClick: () -> Unit,
    onCancelOrderClick: () -> Unit,
    isCreateOrderViaSmsForbidden: Boolean,
    onCreateOrderViaSmsClick: () -> Unit,
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
                    canCreateOrderViaSms = !isCreateOrderViaSmsForbidden &&
                        currentOrderState.sourceOrder.tradingStation.phone != null,
                    onCreateOrderViaSmsClick = { onCreateOrderViaSmsClick() },
                )
                OrderStatusCard(
                    order = currentOrderState,
                    onRepeatOrderClick = { onRepeatOrderClick() },
                    onCancelOrderClick = { onCancelOrderClick() },
                )
                Spacer(modifier = Modifier.height(16.dp))
                OrderStatusHistoryCard(history = currentOrderState.sourceOrder.statusHistory)
                Spacer(modifier = Modifier.height(16.dp))
                OrderDetailsCard(
                    cart = currentOrderState.sourceOrder.cart,
                    comment = currentOrderState.sourceOrder.comment,
                )
            }
        }
    }
}

@Composable
private fun AnimatedNetworkStatusCard(
    networkState: OrderNetworkStatus?,
    canCreateOrderViaSms: Boolean,
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
                    canCreateOrderViaSms = canCreateOrderViaSms,
                    onCreateOrderViaSmsClick = onCreateOrderViaSmsClick,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun NetworkStatusCard(
    networkState: OrderNetworkStatus,
    canCreateOrderViaSms: Boolean,
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
            targetState = networkState.toNetworkStatusCardContent(
                canCreateOrderViaSms = canCreateOrderViaSms,
            ),
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
        NetworkStatusCardIcon.Loading -> CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = colors.primary,
            trackColor = colors.primaryVariant,
            strokeWidth = 4.dp,
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
                        text = order.sourceOrder.statusHistory.denialComment
                            ?: stringResource(R.string.nomad_main_status_denied_reason_unavailable),
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
private fun OrderNetworkStatus.toNetworkStatusCardContent(
    canCreateOrderViaSms: Boolean,
): NetworkStatusCardContent =
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
            action = if (canCreateOrderViaSms) {
                NetworkStatusCardAction.SendSms
            } else {
                NetworkStatusCardAction.None
            },
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
            action = if (canCreateOrderViaSms) {
                NetworkStatusCardAction.RetrySms
            } else {
                NetworkStatusCardAction.None
            },
        )

        OrderNetworkStatus.Updating -> NetworkStatusCardContent(
            title = stringResource(R.string.nomad_main_network_status_update_loading_title),
            body = stringResource(R.string.nomad_main_network_status_update_loading_body),
            iconType = NetworkStatusCardIcon.Loading,
            action = NetworkStatusCardAction.None,
        )

        OrderNetworkStatus.UpdateFailed -> NetworkStatusCardContent(
            title = stringResource(R.string.nomad_main_network_status_waiting_title),
            body = stringResource(R.string.nomad_main_network_status_update_failed_body),
            iconType = NetworkStatusCardIcon.BadConnection,
            action = NetworkStatusCardAction.None,
        )
    }

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
                icon = TMIcons.ShopFilled,
                text = order.sourceOrder.tradingStation.name,
                modifier = Modifier.weight(1f, fill = false),
            )
            OrderOverviewInfo(
                icon = TMIcons.LatitudeFilled,
                text = order.sourceOrder.location.latitude.toDisplayCoordinate(),
            )
            OrderOverviewInfo(
                icon = TMIcons.LongitudeFilled,
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
        isCreateOrderViaSmsForbidden = false,
        onOpenHistoryClick = {},
        onCreateOrderClick = {},
        onRepeatOrderClick = {},
        onCancelOrderClick = {},
        onCreateOrderViaSmsClick = {},
    )
}
