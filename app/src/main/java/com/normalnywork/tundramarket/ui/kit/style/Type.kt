package com.normalnywork.tundramarket.ui.kit.style

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.normalnywork.tundramarket.R

val LocalTMTypography = staticCompositionLocalOf<TundraMarketTypography> {
    error("LocalTMTypography provides nothing")
}

@Composable
private fun juneauFontFamily() = FontFamily(
    Font(R.font.juneau_bold, weight = FontWeight.Bold)
)

@Composable
private fun golosFontFamily() = FontFamily(
    Font(R.font.golos_regular, weight = FontWeight.Normal),
    Font(R.font.golos_medium, weight = FontWeight.Medium),
    Font(R.font.golos_semibold, weight = FontWeight.SemiBold),
)

@Composable
fun provideTundraMarketTypography(): TundraMarketTypography {
    val juneauFont = juneauFontFamily()
    val golosFont = golosFontFamily()

    return TundraMarketTypography(
        title = TextStyle(
            fontSize = 22.sp,
            fontFamily = juneauFont,
            fontWeight = FontWeight.Bold,
        ),
        subtitle = TextStyle(
            fontSize = 16.sp,
            fontFamily = golosFont,
            fontWeight = FontWeight.Medium,
        ),
        body = TextStyle(
            fontSize = 16.sp,
            fontFamily = golosFont,
            fontWeight = FontWeight.Normal,
        ),
        bodySmall = TextStyle(
            fontSize = 14.sp,
            fontFamily = golosFont,
            fontWeight = FontWeight.Normal,
        ),
        caption = TextStyle(
            fontSize = 14.sp,
            fontFamily = golosFont,
            fontWeight = FontWeight.SemiBold,
        ),
        captionLabel = TextStyle(
            fontSize = 12.sp,
            fontFamily = golosFont,
            fontWeight = FontWeight.Medium,
        ),
        label = TextStyle(
            fontSize = 18.sp,
            fontFamily = juneauFont,
            fontWeight = FontWeight.Bold,
        ),
        button = TextStyle(
            fontSize = 18.sp,
            fontFamily = juneauFont,
            fontWeight = FontWeight.Bold,
        ),
        buttonSmall = TextStyle(
            fontSize = 16.sp,
            fontFamily = juneauFont,
            fontWeight = FontWeight.Bold,
        ),
    )
}

data class TundraMarketTypography(
    val title: TextStyle,
    val subtitle: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    val caption: TextStyle,
    val captionLabel: TextStyle,
    val label: TextStyle,
    val button: TextStyle,
    val buttonSmall: TextStyle,
)