package com.normalnywork.tundramarket.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.ui.kit.components.InfoCard
import com.normalnywork.tundramarket.ui.kit.components.TMTimeline
import com.normalnywork.tundramarket.ui.kit.components.TMTopBar
import com.normalnywork.tundramarket.ui.kit.icons.Checkmark
import com.normalnywork.tundramarket.ui.kit.icons.Internet
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.Waiting
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import com.normalnywork.tundramarket.ui.navigation.auth.AuthInitializationComponent
import com.normalnywork.tundramarket.ui.navigation.auth.AuthInitializationComponent.Status

@Composable
fun AuthInitializationContent(component: AuthInitializationComponent) {
    val auth by component.auth.collectAsState()
    val tradingStations by component.tradingStations.collectAsState()
    val catalog by component.catalog.collectAsState()
    val currentOrder by component.currentOrder.collectAsState()

    val colors = LocalTMColors.current

    Scaffold(
        topBar = {
            TMTopBar(title = stringResource(R.string.auth_init_title))
        },
        content = { paddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
                    .padding(16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                InfoCard(
                    title = stringResource(R.string.auth_init_internet_title),
                    body = stringResource(R.string.auth_init_internet_desc),
                    icon = TMIcons.Internet,
                )
                StatusCard(
                    authStatus = auth,
                    tradingStationsStatus = tradingStations,
                    catalogStatus = catalog,
                    currentOrderStatus = currentOrder,
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
    )
}

@Composable
private fun StatusCard(
    authStatus: Status,
    tradingStationsStatus: Status?,
    catalogStatus: Status?,
    currentOrderStatus: Status?,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                shape = TMShapes.Medium,
                color = colors.stroke,
            )
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.auth_init_status_subtitle).uppercase(),
            style = typography.label,
            color = colors.textPrimary,
        )
        val items = listOfNotNull(
            StatusItem(
                title = stringResource(R.string.auth_init_step_auth),
                status = authStatus,
            ),
            tradingStationsStatus?.let {
                StatusItem(
                    title = stringResource(R.string.auth_init_step_stations),
                    status = it,
                )
            },
            catalogStatus?.let {
                StatusItem(
                    title = stringResource(R.string.auth_init_step_catalog),
                    status = it,
                )
            },
            currentOrderStatus?.let {
                StatusItem(
                    title = stringResource(R.string.auth_init_step_current_order),
                    status = it,
                )
            },
        )

        TMTimeline(
            itemsCount = items.size,
            marker = { index ->
                StatusMarker(status = items[index].status)
            },
            content = { index ->
                StatusItemText(item = items[index])
            },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StatusMarker(status: Status) {
    val colors = LocalTMColors.current

    val container by animateColorAsState(
        if (status == Status.Processing) colors.primary
        else colors.primaryVariant
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = container,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(status) {
            if (it == Status.Processing) {
                LoadingIndicator(
                    color = colors.onPrimary,
                    modifier = Modifier.size(24.dp),
                )
            } else {
                Icon(
                    imageVector = when (it) {
                        Status.Queued -> TMIcons.Waiting
                        Status.Done -> TMIcons.Checkmark
                    },
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun StatusItemText(item: StatusItem) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = item.title,
            style = typography.subtitle,
            color = colors.textPrimary,
        )
        AnimatedContent(
            targetState = item.status,
            transitionSpec = {
                slideInVertically { -it } + fadeIn() togetherWith
                        slideOutVertically { it } + fadeOut()
            },
        ) {
            Text(
                text = it.text(),
                style = typography.bodySmall,
                color = colors.textSecondary,
            )
        }
    }
}

private data class StatusItem(
    val title: String,
    val status: Status,
)

@Composable
private fun Status.text(): String {
    return stringResource(
        when (this) {
            Status.Queued -> R.string.auth_init_stage_queued
            Status.Processing -> R.string.auth_init_stage_processing
            Status.Done -> R.string.auth_init_stage_done
        },
    )
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    AuthInitializationContent(
        component = MockAuthInitializationComponent(),
    )
}
