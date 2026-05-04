package com.normalnywork.tundramarket.ui.navigation.auth

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
import com.normalnywork.tundramarket.ui.screens.auth.AuthUserInfoComponent
import com.normalnywork.tundramarket.ui.screens.auth.RoleSelectionContent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun AuthFlowContent(
    component: AuthFlowComponent,
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
            is AuthFlowComponent.Child.RoleSelection -> RoleSelectionContent(instance.component)
            is AuthFlowComponent.Child.UserInfo -> AuthUserInfoContent(instance.component)
        }
    }
}

@Composable
private fun AuthUserInfoContent(component: AuthUserInfoComponent) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "User info: ${component.getRoleLabel()}")
        Button(onClick = component::onAuthorizeClicked) {
            Text(text = "Finish authorization")
        }
        Button(onClick = component::onBackClicked) {
            Text(text = "Back")
        }
    }
}
