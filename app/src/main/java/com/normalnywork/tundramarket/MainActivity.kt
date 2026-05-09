package com.normalnywork.tundramarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.defaultComponentContext
import com.normalnywork.tundramarket.domain.usecases.auth.GetUserRoleUseCase
import com.normalnywork.tundramarket.ui.kit.style.TundraMarketTheme
import com.normalnywork.tundramarket.ui.navigation.RootComponent
import com.normalnywork.tundramarket.ui.navigation.RootContent
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val rootComponentFactory: RootComponent.Factory by inject()
    private val getUserRoleUseCase: GetUserRoleUseCase by inject()

    private var keepSplashOnScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
            .setKeepOnScreenCondition { keepSplashOnScreen }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            val userRole = runCatching { getUserRoleUseCase() }.getOrNull()

            val rootComponent = rootComponentFactory(
                componentContext = defaultComponentContext(),
                userRole = userRole,
            )

            setContent {
                TundraMarketTheme {
                    RootContent(component = rootComponent)
                }
            }

            keepSplashOnScreen = false
        }
    }
}
