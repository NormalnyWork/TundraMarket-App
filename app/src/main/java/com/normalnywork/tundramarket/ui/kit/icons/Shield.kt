package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.Shield: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Icons",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(10.95f, 15.55f)
            lineTo(16.6f, 9.9f)
            lineTo(15.175f, 8.475f)
            lineTo(10.95f, 12.7f)
            lineTo(8.85f, 10.6f)
            lineTo(7.425f, 12.025f)
            lineTo(10.95f, 15.55f)
            close()
            moveTo(12f, 22f)
            curveTo(9.683f, 21.417f, 7.771f, 20.087f, 6.262f, 18.013f)
            curveTo(4.754f, 15.938f, 4f, 13.633f, 4f, 11.1f)
            verticalLineTo(5f)
            lineTo(12f, 2f)
            lineTo(20f, 5f)
            verticalLineTo(11.1f)
            curveTo(20f, 13.633f, 19.246f, 15.938f, 17.737f, 18.013f)
            curveTo(16.229f, 20.087f, 14.317f, 21.417f, 12f, 22f)
            close()
            moveTo(12f, 19.9f)
            curveTo(13.733f, 19.35f, 15.167f, 18.25f, 16.3f, 16.6f)
            curveTo(17.433f, 14.95f, 18f, 13.117f, 18f, 11.1f)
            verticalLineTo(6.375f)
            lineTo(12f, 4.125f)
            lineTo(6f, 6.375f)
            verticalLineTo(11.1f)
            curveTo(6f, 13.117f, 6.567f, 14.95f, 7.7f, 16.6f)
            curveTo(8.833f, 18.25f, 10.267f, 19.35f, 12f, 19.9f)
            close()
        }
    }.build()
}
