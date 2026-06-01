package com.normalnywork.tundramarket.ui.navigation.nomad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.PredictiveBackParams
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatableV2
import com.normalnywork.tundramarket.ui.navigation.android13NavigationTransition
import com.normalnywork.tundramarket.ui.screens.nomad.NomadHistoryComponent
import com.normalnywork.tundramarket.ui.screens.nomad.NomadMainComponent
import com.normalnywork.tundramarket.ui.screens.nomad.NomadOrderDetailsComponent
import com.normalnywork.tundramarket.ui.screens.nomad.neworder.NomadCreateOrderContent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun NomadFlowContent(
    component: NomadFlowComponent,
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
            is NomadFlowComponent.Child.Main -> NomadMainContent(instance.component)
            is NomadFlowComponent.Child.History -> NomadHistoryContent(instance.component)
            is NomadFlowComponent.Child.OrderDetails -> NomadOrderDetailsContent(instance.component)
            is NomadFlowComponent.Child.CreateOrder -> NomadCreateOrderContent(instance.component)
        }
    }
}

@Composable
private fun NomadMainContent(component: NomadMainComponent) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Nomad main")
        Text(text = "Active or previous order details are displayed on this screen.")
        Button(onClick = component::onOpenHistoryClicked) {
            Text(text = "Open history")
        }
        Button(onClick = component::onCreateOrderClicked) {
            Text(text = "Create order")
        }
    }
}

@Composable
private fun NomadHistoryContent(component: NomadHistoryComponent) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Nomad history")
        Button(onClick = component::onOpenOrderDetailsClicked) {
            Text(text = "Open history order")
        }
        Button(onClick = component::onBackClicked) {
            Text(text = "Back")
        }
    }
}

@Composable
private fun NomadOrderDetailsContent(component: NomadOrderDetailsComponent) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Nomad order details: ${component.orderId}")
        Button(onClick = component::onBackClicked) {
            Text(text = "Back")
        }
    }
}

