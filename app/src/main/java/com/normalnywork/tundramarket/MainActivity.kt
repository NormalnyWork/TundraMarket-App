package com.normalnywork.tundramarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.defaultComponentContext
import com.normalnywork.tundramarket.domain.entities.AppStartDestination
import com.normalnywork.tundramarket.domain.usecases.auth.GetAppStartDestinationUseCase
import com.normalnywork.tundramarket.ui.kit.style.TundraMarketTheme
import com.normalnywork.tundramarket.ui.navigation.RootComponent
import com.normalnywork.tundramarket.ui.navigation.RootContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val rootComponentFactory: RootComponent.Factory by inject()
    private val getAppStartDestinationUseCase: GetAppStartDestinationUseCase by inject()

    private val startupScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    private var keepSplashOnScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
            .setKeepOnScreenCondition { keepSplashOnScreen }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        startupScope.launch {
            val startDestination = runCatching { getAppStartDestinationUseCase() }
                .getOrElse { error ->
                    error.printStackTrace()
                    AppStartDestination.Auth
                }

            val rootComponent = rootComponentFactory(
                componentContext = defaultComponentContext(),
                startDestination = startDestination,
            )

            setContent {
                TundraMarketTheme {
                    RootContent(component = rootComponent)
                }
            }

            keepSplashOnScreen = false
        }
    }

    override fun onDestroy() {
        startupScope.cancel()
        super.onDestroy()
    }
}
