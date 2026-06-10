package com.normalnywork.tundramarket.ui.navigation.auth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.PredictiveBackParams
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatableV2
import com.normalnywork.tundramarket.ui.navigation.android13NavigationTransition
import com.normalnywork.tundramarket.ui.screens.auth.AuthInitializationContent
import com.normalnywork.tundramarket.ui.screens.auth.AuthSmsPermissionContent
import com.normalnywork.tundramarket.ui.screens.auth.AuthUserInfoContent
import com.normalnywork.tundramarket.ui.screens.auth.RoleSelectionContent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun AuthFlowContent(
    component: AuthFlowComponent,
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
            is AuthFlowComponent.Child.RoleSelection -> RoleSelectionContent(instance.component)
            is AuthFlowComponent.Child.UserInfo -> AuthUserInfoContent(instance.component)
            is AuthFlowComponent.Child.Initialization -> AuthInitializationContent(instance.component)
            is AuthFlowComponent.Child.SmsPermission -> AuthSmsPermissionContent(instance.component)
        }
    }
}
