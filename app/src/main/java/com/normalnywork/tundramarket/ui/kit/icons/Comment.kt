package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.Comment: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Comment",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(6f, 14f)
            horizontalLineTo(14f)
            verticalLineTo(12f)
            horizontalLineTo(6f)
            verticalLineTo(14f)
            close()
            moveTo(6f, 11f)
            horizontalLineTo(18f)
            verticalLineTo(9f)
            horizontalLineTo(6f)
            verticalLineTo(11f)
            close()
            moveTo(6f, 8f)
            horizontalLineTo(18f)
            verticalLineTo(6f)
            horizontalLineTo(6f)
            verticalLineTo(8f)
            close()
            moveTo(2f, 22f)
            verticalLineTo(4f)
            curveTo(2f, 3.45f, 2.196f, 2.979f, 2.588f, 2.588f)
            curveTo(2.979f, 2.196f, 3.45f, 2f, 4f, 2f)
            horizontalLineTo(20f)
            curveTo(20.55f, 2f, 21.021f, 2.196f, 21.413f, 2.588f)
            curveTo(21.804f, 2.979f, 22f, 3.45f, 22f, 4f)
            verticalLineTo(16f)
            curveTo(22f, 16.55f, 21.804f, 17.021f, 21.413f, 17.413f)
            curveTo(21.021f, 17.804f, 20.55f, 18f, 20f, 18f)
            horizontalLineTo(6f)
            lineTo(2f, 22f)
            close()
            moveTo(5.15f, 16f)
            horizontalLineTo(20f)
            verticalLineTo(4f)
            horizontalLineTo(4f)
            verticalLineTo(17.125f)
            lineTo(5.15f, 16f)
            close()
        }
    }.build()
}
