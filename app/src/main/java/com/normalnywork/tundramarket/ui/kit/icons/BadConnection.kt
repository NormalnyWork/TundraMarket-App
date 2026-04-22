package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.BadConnection: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "BadConnection",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(2f, 22f)
            lineTo(22f, 2f)
            verticalLineTo(8f)
            horizontalLineTo(20f)
            verticalLineTo(6.825f)
            lineTo(6.825f, 20f)
            horizontalLineTo(18f)
            verticalLineTo(22f)
            horizontalLineTo(2f)
            close()
            moveTo(20.288f, 21.712f)
            curveTo(20.096f, 21.521f, 20f, 21.283f, 20f, 21f)
            curveTo(20f, 20.717f, 20.096f, 20.479f, 20.288f, 20.288f)
            curveTo(20.479f, 20.096f, 20.717f, 20f, 21f, 20f)
            curveTo(21.283f, 20f, 21.521f, 20.096f, 21.712f, 20.288f)
            curveTo(21.904f, 20.479f, 22f, 20.717f, 22f, 21f)
            curveTo(22f, 21.283f, 21.904f, 21.521f, 21.712f, 21.712f)
            curveTo(21.521f, 21.904f, 21.283f, 22f, 21f, 22f)
            curveTo(20.717f, 22f, 20.479f, 21.904f, 20.288f, 21.712f)
            close()
            moveTo(20f, 18f)
            verticalLineTo(10f)
            horizontalLineTo(22f)
            verticalLineTo(18f)
            horizontalLineTo(20f)
            close()
        }
    }.build()
}
