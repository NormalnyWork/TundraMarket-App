package com.normalnywork.tundramarket.ui.kit.style

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalTMColors = staticCompositionLocalOf<TundraMarketColors> {
    error("LocalTMColors provides nothing")
}

data class TundraMarketColors(
    val primary: Color,
    val primaryVariant: Color,
    val onPrimary: Color,
    val secondary: Color,
    val secondaryVariant: Color,
    val background: Color,
    val backgroundCard: Color,
    val stroke: Color,
    val textPrimary: Color,
    val textSecondary: Color,
) {

    companion object {

        val Light = TundraMarketColors(
            primary = Color(0xFFD01039),
            primaryVariant = Color(0x1AD01039),
            onPrimary = Color(0xFFFFFFFF),
            secondary = Color(0xFFF9F0E1),
            secondaryVariant = Color(0x80F9F0E1),
            background = Color(0xFFFFFFFF),
            backgroundCard = Color(0x80F0EAED),
            stroke = Color(0xFFEADFE5),
            textPrimary = Color(0xFF000000),
            textSecondary = Color(0x99000000),
        )
    }
}