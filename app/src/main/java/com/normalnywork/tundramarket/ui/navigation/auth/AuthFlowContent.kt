package com.normalnywork.tundramarket.ui.navigation.auth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.normalnywork.tundramarket.ui.screens.auth.AuthUserInfoContent
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
