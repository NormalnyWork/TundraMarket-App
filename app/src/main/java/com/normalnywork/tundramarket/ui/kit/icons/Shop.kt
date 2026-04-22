package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.Shop: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Shop",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(4f, 6f)
            verticalLineTo(4f)
            horizontalLineTo(20f)
            verticalLineTo(6f)
            horizontalLineTo(4f)
            close()
            moveTo(4f, 20f)
            verticalLineTo(14f)
            horizontalLineTo(3f)
            verticalLineTo(12f)
            lineTo(4f, 7f)
            horizontalLineTo(20f)
            lineTo(21f, 12f)
            verticalLineTo(14f)
            horizontalLineTo(20f)
            verticalLineTo(20f)
            horizontalLineTo(18f)
            verticalLineTo(14f)
            horizontalLineTo(14f)
            verticalLineTo(20f)
            horizontalLineTo(4f)
            close()
            moveTo(6f, 18f)
            horizontalLineTo(12f)
            verticalLineTo(14f)
            horizontalLineTo(6f)
            verticalLineTo(18f)
            close()
            moveTo(5.05f, 12f)
            horizontalLineTo(18.95f)
            lineTo(18.35f, 9f)
            horizontalLineTo(5.65f)
            lineTo(5.05f, 12f)
            close()
        }
    }.build()
}
