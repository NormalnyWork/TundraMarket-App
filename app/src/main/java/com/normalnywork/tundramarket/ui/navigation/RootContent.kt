package com.normalnywork.tundramarket.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.PredictiveBackParams
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatableV2
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.navigation.RootComponent.Child
import com.normalnywork.tundramarket.ui.navigation.auth.AuthFlowContent
import com.normalnywork.tundramarket.ui.navigation.nomad.NomadFlowContent
import com.normalnywork.tundramarket.ui.navigation.tradingstation.TradingStationFlowContent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    ChildStack(
        stack = component.childStack,
        modifier = modifier
            .fillMaxSize()
            .background(LocalTMColors.current.secondary),
        animation = stackAnimation(
            predictiveBackParams = {
                PredictiveBackParams(
                    backHandler = component.backHandler,
                    onBack = component::onBackClicked,
                    animatable = ::androidPredictiveBackAnimatableV2
                )
            },
            selector = { _, _, _, _ -> android13NavigationTransition()}
        )
    ) { child ->
        when (val instance = child.instance) {
            is Child.Auth -> AuthFlowContent(component = instance.component)
            is Child.Nomad -> NomadFlowContent(component = instance.component)
            is Child.TradingStation -> TradingStationFlowContent(component = instance.component)
        }
    }
}
