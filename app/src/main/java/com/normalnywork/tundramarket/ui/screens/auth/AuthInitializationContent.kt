package com.normalnywork.tundramarket.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.R
import com.normalnywork.tundramarket.ui.kit.components.InfoCard
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
        Column {
            StatusItem(
                title = stringResource(R.string.auth_init_step_auth),
                status = authStatus,
            )
            if (tradingStationsStatus != null) {
                StatusItemsDivider()
                StatusItem(
                    title = stringResource(R.string.auth_init_step_stations),
                    status = tradingStationsStatus,
                )
            }
            if (catalogStatus != null) {
                StatusItemsDivider()
                StatusItem(
                    title = stringResource(R.string.auth_init_step_catalog),
                    status = catalogStatus,
                )
            }
        }
    }
}

@Composable
private fun StatusItemsDivider() {
    val color = LocalTMColors.current.primaryVariant

    Canvas(
        modifier = Modifier
            .padding(horizontal = 19.dp)
            .size(width = 2.dp, height = 12.dp)
    ) {
        val strokeWidth = size.width

        drawLine(
            color = color,
            start = Offset(x = size.width / 2, y = 0f),
            end = Offset(x = size.width / 2, y = size.height),
            strokeWidth = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(
                    2.dp.toPx(),
                    2.dp.toPx(),
                )
            ),
        )
    }
}

@Composable
private fun StatusItem(
    title: String,
    status: Status,
) {
    val colors = LocalTMColors.current
    val typography = LocalTMTypography.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
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
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = typography.subtitle,
                color = colors.textPrimary,
            )
            AnimatedContent(
                targetState = status,
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
}

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
