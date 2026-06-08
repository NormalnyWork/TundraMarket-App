package com.normalnywork.tundramarket.ui.navigation.tradingstation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.PredictiveBackParams
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatableV2
import com.normalnywork.tundramarket.ui.navigation.android13NavigationTransition
import com.normalnywork.tundramarket.ui.screens.tradingstation.TradingStationMainContent
import com.normalnywork.tundramarket.ui.screens.tradingstation.TradingStationOrderDetailsContent

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
