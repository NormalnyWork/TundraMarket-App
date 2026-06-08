package com.normalnywork.tundramarket.ui.screens.tradingstation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.normalnywork.tundramarket.ui.kit.components.TMTopBar
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.shared.OrderDetailsCard
import com.normalnywork.tundramarket.ui.shared.OrderStatusHistoryCard

@Composable
fun TradingStationOrderDetailsContent(component: TradingStationOrderDetailsComponent) {
    TradingStationOrderDetailsContent(
        order = component.order,
        onBackClick = component::onBackClicked,
    )
}

@Composable
private fun TradingStationOrderDetailsContent(
    order: Order,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TMTopBar(
                title = stringResource(R.string.trading_station_order_details_title, order.id),
                onBack = onBackClick,
            )
        },
        containerColor = LocalTMColors.current.background,
        modifier = Modifier.fillMaxSize(),
    ) { paddings ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                OrderDetailsCard(
                    cart = order.cart,
                    comment = order.comment,
                    location = order.location,
                )
            }
            item {
                OrderStatusHistoryCard(history = order.statusHistory)
            }
        }
    }
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun PreviewOrderDetails() {
    TradingStationOrderDetailsContent(
        order = sampleTradingStationDetailsOrder(),
        onBackClick = {},
    )
}

private fun sampleTradingStationDetailsOrder() = Order(
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
            name = "Крупа",
            details = null,
            weight = 1f,
            volume = 1f,
        ) to 2,
        Product(
            id = 2,
            name = "Чай",
            details = null,
            weight = 0.2f,
            volume = 0.4f,
        ) to 1,
    ),
    location = Location(
        latitude = 67.983f,
        longitude = 68.589f,
    ),
    comment = "Оставить заказ у северного входа в чум",
    status = OrderStatus.Processing,
    statusHistory = listOf(
        OrderStatusHistory(
            status = OrderStatus.Created,
            time = System.currentTimeMillis() - 3_600_000,
        ),
        OrderStatusHistory(
            status = OrderStatus.Processing,
            time = System.currentTimeMillis(),
        ),
    ),
)
