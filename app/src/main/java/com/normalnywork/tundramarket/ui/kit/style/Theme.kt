package com.normalnywork.tundramarket.ui.kit.style

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun TundraMarketTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        CompositionLocalProvider(
            LocalTMTypography provides provideTundraMarketTypography(),
            LocalTMColors provides TundraMarketColors.Light,
            content = content,
        )
    }
}