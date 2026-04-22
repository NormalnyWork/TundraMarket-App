package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.Plus: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Plus",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(11f, 21f)
            verticalLineTo(13f)
            horizontalLineTo(3f)
            verticalLineTo(11f)
            horizontalLineTo(11f)
            verticalLineTo(3f)
            horizontalLineTo(13f)
            verticalLineTo(11f)
            horizontalLineTo(21f)
            verticalLineTo(13f)
            horizontalLineTo(13f)
            verticalLineTo(21f)
            horizontalLineTo(11f)
            close()
        }
    }.build()
}
