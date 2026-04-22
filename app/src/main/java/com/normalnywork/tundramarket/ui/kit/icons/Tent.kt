package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.Tent: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Tent",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(2f, 22f)
            verticalLineTo(17.35f)
            lineTo(10.75f, 5.55f)
            lineTo(9f, 3.2f)
            lineTo(10.6f, 2f)
            lineTo(12f, 3.875f)
            lineTo(13.4f, 2f)
            lineTo(15f, 3.2f)
            lineTo(13.25f, 5.55f)
            lineTo(22f, 17.35f)
            verticalLineTo(22f)
            horizontalLineTo(2f)
            close()
            moveTo(12f, 7.225f)
            lineTo(4f, 18f)
            verticalLineTo(20f)
            horizontalLineTo(7f)
            lineTo(12f, 13f)
            lineTo(17f, 20f)
            horizontalLineTo(20f)
            verticalLineTo(18f)
            lineTo(12f, 7.225f)
            close()
            moveTo(9.45f, 20f)
            horizontalLineTo(14.55f)
            lineTo(12f, 16.45f)
            lineTo(9.45f, 20f)
            close()
        }
    }.build()
}
