package com.normalnywork.tundramarket.ui.kit.style

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider

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

class TMPreviewWrapperProvider : PreviewWrapperProvider {

    @Composable
    override fun Wrap(content: @Composable (() -> Unit)) {
        TundraMarketTheme(content)
    }
}