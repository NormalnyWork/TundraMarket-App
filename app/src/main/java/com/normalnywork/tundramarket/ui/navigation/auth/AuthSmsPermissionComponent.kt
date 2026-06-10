package com.normalnywork.tundramarket.ui.navigation.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthSmsPermissionComponent {

    val deniedAttempts: StateFlow<Int>

    fun onPermissionResult(isGranted: Boolean)
}
