package com.normalnywork.tundramarket.ui.navigation.tradingstation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.PredictiveBackParams
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatableV2
import com.normalnywork.tundramarket.domain.entities.TradingStationOrdersPage
import com.normalnywork.tundramarket.ui.navigation.android13NavigationTransition
import com.normalnywork.tundramarket.ui.screens.tradingstation.TradingStationMainComponent
import com.normalnywork.tundramarket.ui.screens.tradingstation.TradingStationOrderDetailsComponent
import com.normalnywork.tundramarket.ui.screens.tradingstation.TradingStationOrdersPageComponent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun TradingStationFlowContent(
    component: TradingStationFlowComponent,
    modifier: Modifier = Modifier,
) {
    ChildStack(
        stack = component.childStack,
        modifier = modifier.fillMaxSize(),
        animation = stackAnimation(
            predictiveBackParams = {
                PredictiveBackParams(
                    backHandler = component.backHandler,
                    onBack = component::onBackClicked,
                    animatable = ::androidPredictiveBackAnimatableV2
                )
            },
            selector = { _, _, _, _ -> android13NavigationTransition() }
        )
    ) { child ->
        when (val instance = child.instance) {
            is TradingStationFlowComponent.Child.Main -> TradingStationMainContent(instance.component)
            is TradingStationFlowComponent.Child.OrderDetails -> {
                TradingStationOrderDetailsContent(instance.component)
            }
        }
    }
}

@Composable
private fun TradingStationMainContent(component: TradingStationMainComponent) {
    Column(modifier = Modifier.fillMaxSize()) {
        val pages = component.childPages.value

        PrimaryTabRow(selectedTabIndex = pages.selectedIndex) {
            pages.items.forEachIndexed { index, child ->
                Tab(
                    selected = index == pages.selectedIndex,
                    onClick = { component.onPageSelected(index) },
                    text = { Text(text = child.configuration.toTitle()) },
                )
            }
        }

        ChildPages(
            pages = component.childPages,
            onPageSelected = component::onPageSelected,
            modifier = Modifier.weight(1f),
        ) { _, pageComponent ->
            TradingStationOrdersPageContent(pageComponent)
        }
    }
}

@Composable
private fun TradingStationOrdersPageContent(component: TradingStationOrdersPageComponent) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = component.page.toTitle())
        Button(onClick = component::onOpenOrderDetailsClicked) {
            Text(text = "Open order details")
        }
    }
}

@Composable
private fun TradingStationOrderDetailsContent(component: TradingStationOrderDetailsComponent) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Trading station order details: ${component.orderId}")
        Button(onClick = component::onBackClicked) {
            Text(text = "Back")
        }
    }
}

private fun TradingStationOrdersPage.toTitle(): String =
    when (this) {
        TradingStationOrdersPage.Active -> "Active orders"
        TradingStationOrdersPage.New -> "New orders"
        TradingStationOrdersPage.History -> "History orders"
    }
