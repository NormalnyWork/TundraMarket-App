package com.normalnywork.tundramarket.ui.screens.auth

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.normalnywork.tundramarket.ui.navigation.auth.AuthSmsPermissionComponent
import com.normalnywork.tundramarket.ui.tools.BaseStateHolder
import kotlinx.coroutines.flow.MutableStateFlow

class ActualAuthSmsPermissionComponent(
    componentContext: ComponentContext,
    private val proceed: () -> Unit,
) : AuthSmsPermissionComponent, ComponentContext by componentContext {

    private val stateHolder = instanceKeeper.getOrCreate {
        StateHolder()
    }

    override val deniedAttempts = stateHolder.deniedAttempts

    override fun onPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            proceed()
            return
        }

        if (deniedAttempts.value == 0) {
            deniedAttempts.value = 1
        } else {
            proceed()
        }
    }

    private class StateHolder : BaseStateHolder() {

        val deniedAttempts = MutableStateFlow(0)
    }
}

class MockAuthSmsPermissionComponent : AuthSmsPermissionComponent {

    override val deniedAttempts = MutableStateFlow(0)

    override fun onPermissionResult(isGranted: Boolean) = Unit
}
