package com.normalnywork.tundramarket.ui.screens.nomad

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.normalnywork.tundramarket.ui.kit.components.TMButtonSlider
import com.normalnywork.tundramarket.ui.kit.components.TMTopBar
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.shared.OrderDetailsCard
import com.normalnywork.tundramarket.ui.shared.OrderStatusHistoryCard

@Composable
fun NomadOrderDetailsContent(component: NomadOrderDetailsComponent) {
    val hasOngoingOrder by component.hasOngoingOrder.collectAsState()

    NomadOrderDetailsContent(
        order = component.order,
        onBackClick = component::onBackClicked,
        onRepeatOrderClick = component::onRepeatOrderClicked
            .takeIf { !hasOngoingOrder },
    )
}

@Composable
private fun NomadOrderDetailsContent(
    order: Order,
    onBackClick: () -> Unit,
    onRepeatOrderClick: (() -> Unit)? = null,
) {
    val colors = LocalTMColors.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TMTopBar(
                title = stringResource(R.string.nomad_history_order_title, order.id),
                onBack = onBackClick,
                showDivider = scrollState.canScrollBackward,
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = onRepeatOrderClick != null,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
            ) {
                NomadOrderDetailsBottomBar(
                    showDivider = scrollState.canScrollForward,
                    onRepeatOrderClick = { onRepeatOrderClick?.invoke() },
                )
            }
        },
        containerColor = colors.background,
        modifier = Modifier.fillMaxSize(),
    ) { paddings ->
        NomadOrderDetailsCards(
            order = order,
            scrollState = scrollState,
            paddings = paddings,
        )
    }
}

@Composable
private fun NomadOrderDetailsCards(
    order: Order,
    scrollState: ScrollState,
    paddings: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(paddings),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OrderDetailsCard(
            cart = order.cart,
            comment = order.comment,
            location = order.location,
            tradingStation = order.tradingStation,
        )
        OrderStatusHistoryCard(history = order.statusHistory)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun NomadOrderDetailsBottomBar(
    showDivider: Boolean,
    onRepeatOrderClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
            color = if (showDivider) {
                LocalTMColors.current.stroke
            } else {
                LocalTMColors.current.background
            },
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
        ) {
            TMButtonSlider(
                text = stringResource(R.string.nomad_main_repeat_order_action),
                onClick = onRepeatOrderClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun NomadOrderDetailsPreview() {
    NomadOrderDetailsContent(
        order = sampleOrderDetails(),
        onBackClick = {},
        onRepeatOrderClick = {},
    )
}

private fun sampleOrderDetails() = Order(
    id = 172,
    nomadId = 7,
    tradingStation = TradingStation(
        id = 2,
        name = "Паюта",
        phone = null,
        location = Location(
            latitude = 67.12f,
            longitude = 68.72f,
        ),
    ),
    cart = listOf(
        Product(
            id = 1,
            name = "Хлеб",
            details = null,
            weight = 1f,
            volume = 1f,
        ) to 2,
        Product(
            id = 2,
            name = "Молоко",
            details = null,
            weight = 1f,
            volume = 1f,
        ) to 1,
    ),
    location = Location(
        latitude = 67.9914f,
        longitude = 68.5914f,
    ),
    comment = "",
    status = OrderStatus.Sent,
    statusHistory = listOf(
        OrderStatusHistory(
            status = OrderStatus.Created,
            time = System.currentTimeMillis() - 3_600_000,
        ),
        OrderStatusHistory(
            status = OrderStatus.Processing,
            time = System.currentTimeMillis() - 3_000_000,
        ),
        OrderStatusHistory(
            status = OrderStatus.Sent,
            time = System.currentTimeMillis() - 1_800_000,
        ),
    ),
)
