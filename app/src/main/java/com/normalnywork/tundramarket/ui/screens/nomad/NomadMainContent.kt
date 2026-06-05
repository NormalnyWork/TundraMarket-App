package com.normalnywork.tundramarket.ui.screens.nomad

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.ui.kit.components.TMButtonPrimary
import com.normalnywork.tundramarket.ui.kit.components.TMCompactTopBar
import com.normalnywork.tundramarket.ui.kit.icons.ChevronRight
import com.normalnywork.tundramarket.ui.kit.icons.IllustrationDroneTent
import com.normalnywork.tundramarket.ui.kit.icons.Latitude
import com.normalnywork.tundramarket.ui.kit.icons.Longitude
import com.normalnywork.tundramarket.ui.kit.icons.Shop
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.Waiting
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import com.normalnywork.tundramarket.ui.tools.toDisplayCoordinate

@Composable
fun NomadMainContent(component: NomadMainComponent) {
    val currentOrderState by component.currentOrderState.collectAsState()

    NomadMainContent(
        currentOrderState = currentOrderState,
        onOpenHistoryClick = component::onOpenHistoryClicked,
        onCreateOrderClick = component::onCreateOrderClicked,
    )
}

@Composable
private fun NomadMainContent(
    currentOrderState: NomadMainComponent.CurrentOrderState,
    onOpenHistoryClick: () -> Unit,
    onCreateOrderClick: () -> Unit,
) {
    val colors = LocalTMColors.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Column {
                TMCompactTopBar(title = stringResource(R.string.nomad_main_title))

                (currentOrderState as? NomadMainComponent.CurrentOrderState.Order)
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
                modifier = Modifier.padding(paddings),
            )
        },
        bottomBar = {
            NomadMainBottomCard(
                showDivider = scrollState.canScrollForward,
                onOpenHistoryClick = onOpenHistoryClick,
            )
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
    )
}

@Composable
private fun NomadMainMiddleContent(
    scrollState: ScrollState,
    currentOrderState: NomadMainComponent.CurrentOrderState,
    onCreateOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LocalTMColors.current
    LocalTMTypography.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (currentOrderState is NomadMainComponent.CurrentOrderState.Empty) {
            Arrangement.Center
        } else {
            Arrangement.spacedBy(16.dp)
        },
    ) {
        when (currentOrderState) {
            NomadMainComponent.CurrentOrderState.Empty -> EmptyOrderContent(
                onCreateOrderClick = onCreateOrderClick,
            )

            NomadMainComponent.CurrentOrderState.Loading ->
                CurrentOrderStateText(currentOrderState)

            is NomadMainComponent.CurrentOrderState.Order -> Unit
        }
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
private fun NomadMainBottomCard(
    showDivider: Boolean,
    onOpenHistoryClick: () -> Unit,
) {
    val colors = LocalTMColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background)
            .navigationBarsPadding(),
    ) {
        if (showDivider) HorizontalDivider(color = colors.stroke)

        Row(
            modifier = Modifier
                .padding(16.dp)
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
    }
}

@Composable
private fun CurrentOrderOverviewCard(order: NomadMainComponent.CurrentOrderState.Order) {
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
                text = order.tradingStationName,
                modifier = Modifier.weight(1f, fill = false),
            )
            OrderOverviewInfo(
                icon = TMIcons.Latitude,
                text = order.latitude.toDisplayCoordinate(),
            )
            OrderOverviewInfo(
                icon = TMIcons.Longitude,
                text = order.longitude.toDisplayCoordinate(),
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

@Composable
private fun CurrentOrderStateText(state: NomadMainComponent.CurrentOrderState) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current
    val text = when (state) {
        NomadMainComponent.CurrentOrderState.Loading ->
            stringResource(R.string.nomad_main_order_status_loading)

        NomadMainComponent.CurrentOrderState.Empty ->
            stringResource(R.string.nomad_main_order_status_empty)

        is NomadMainComponent.CurrentOrderState.Order -> {
            val statusText = when (state.syncState) {
                NomadMainComponent.SyncState.Enqueued ->
                    stringResource(R.string.nomad_main_order_status_enqueued)

                NomadMainComponent.SyncState.Processing ->
                    stringResource(R.string.nomad_main_order_status_processing)

                NomadMainComponent.SyncState.Failed ->
                    stringResource(R.string.nomad_main_order_status_failed)

                NomadMainComponent.SyncState.Created ->
                    stringResource(R.string.nomad_main_order_status_created)
            }

            stringResource(
                R.string.nomad_main_order_status_order,
                state.id,
                statusText,
            )
        }
    }

    Text(
        text = text,
        style = typography.body,
        color = colors.textPrimary,
    )
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun NomadMainContentPreview() {
    NomadMainContent(
        currentOrderState = NomadMainComponent.CurrentOrderState.Empty,
        onOpenHistoryClick = {},
        onCreateOrderClick = {},
    )
}
