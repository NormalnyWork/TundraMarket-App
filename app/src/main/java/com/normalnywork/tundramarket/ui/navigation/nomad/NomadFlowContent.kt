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
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.normalnywork.tundramarket.ui.screens.nomad.NomadCreateOrderComponent
import com.normalnywork.tundramarket.ui.screens.nomad.NomadHistoryComponent
import com.normalnywork.tundramarket.ui.screens.nomad.NomadMainComponent
import com.normalnywork.tundramarket.ui.screens.nomad.NomadOrderDetailsComponent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun NomadFlowContent(
    component: NomadFlowComponent,
    modifier: Modifier = Modifier,
) {
    Children(
        stack = component.childStack,
        modifier = modifier.fillMaxSize(),
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            onBack = component::onBackClicked,
            fallbackAnimation = stackAnimation(fade()),
        ),
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

@Composable
private fun NomadCreateOrderContent(component: NomadCreateOrderComponent) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Nomad create order")
        Button(onClick = component::onBackClicked) {
            Text(text = "Back")
        }
    }
}
