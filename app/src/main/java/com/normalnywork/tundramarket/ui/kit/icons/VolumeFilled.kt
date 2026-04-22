package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.VolumeFilled: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "VolumeFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(6.725f, 12.875f)
            curveTo(7.442f, 12.492f, 8.192f, 12.2f, 8.975f, 12f)
            curveTo(9.758f, 11.8f, 10.558f, 11.7f, 11.375f, 11.7f)
            curveTo(11.858f, 11.7f, 12.333f, 11.738f, 12.8f, 11.813f)
            curveTo(13.267f, 11.887f, 13.725f, 11.992f, 14.175f, 12.125f)
            curveTo(14.692f, 12.275f, 15.179f, 12.392f, 15.637f, 12.475f)
            curveTo(16.096f, 12.558f, 16.517f, 12.6f, 16.9f, 12.6f)
            horizontalLineTo(17.325f)
            lineTo(18.35f, 4.2f)
            horizontalLineTo(5.65f)
            lineTo(6.725f, 12.875f)
            close()
            moveTo(7.575f, 21.6f)
            curveTo(7.125f, 21.6f, 6.733f, 21.45f, 6.4f, 21.15f)
            curveTo(6.067f, 20.85f, 5.867f, 20.475f, 5.8f, 20.025f)
            lineTo(3.6f, 2.4f)
            horizontalLineTo(20.4f)
            lineTo(18.2f, 20.025f)
            curveTo(18.133f, 20.475f, 17.933f, 20.85f, 17.6f, 21.15f)
            curveTo(17.267f, 21.45f, 16.875f, 21.6f, 16.425f, 21.6f)
            horizontalLineTo(7.575f)
            close()
        }
    }.build()
}
