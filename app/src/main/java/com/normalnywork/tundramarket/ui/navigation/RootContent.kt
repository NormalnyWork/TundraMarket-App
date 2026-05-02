package com.normalnywork.tundramarket.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.normalnywork.tundramarket.ui.navigation.auth.AuthFlowContent
import com.normalnywork.tundramarket.ui.navigation.nomad.NomadFlowContent
import com.normalnywork.tundramarket.ui.navigation.tradingstation.TradingStationFlowContent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    Children(
        stack = component.childStack,
        modifier = modifier.fillMaxSize(),
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            onBack = {},
            fallbackAnimation = stackAnimation(fade()),
        ),
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Auth -> AuthFlowContent(component = instance.component)
            is RootComponent.Child.Nomad -> NomadFlowContent(component = instance.component)
            is RootComponent.Child.TradingStation -> TradingStationFlowContent(component = instance.component)
        }
    }
}
