package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.ChevronRight: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "ChevronRight",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(12.6f, 12f)
            lineTo(8f, 7.4f)
            lineTo(9.4f, 6f)
            lineTo(15.4f, 12f)
            lineTo(9.4f, 18f)
            lineTo(8f, 16.6f)
            lineTo(12.6f, 12f)
            close()
        }
    }.build()
}
