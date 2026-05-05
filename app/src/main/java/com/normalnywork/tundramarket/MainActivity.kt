package com.normalnywork.tundramarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.normalnywork.tundramarket.ui.kit.style.TundraMarketTheme
import com.normalnywork.tundramarket.ui.navigation.RootComponent
import com.normalnywork.tundramarket.ui.navigation.RootContent
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val rootComponentFactory: RootComponent.Factory by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val rootComponent = rootComponentFactory(componentContext = defaultComponentContext())
        setContent {
            TundraMarketTheme {
                RootContent(component = rootComponent)
            }
        }
    }
}
