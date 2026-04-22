package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.Processing: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Processing",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(8f, 20f)
            horizontalLineTo(16f)
            verticalLineTo(17f)
            curveTo(16f, 15.9f, 15.608f, 14.958f, 14.825f, 14.175f)
            curveTo(14.042f, 13.392f, 13.1f, 13f, 12f, 13f)
            curveTo(10.9f, 13f, 9.958f, 13.392f, 9.175f, 14.175f)
            curveTo(8.392f, 14.958f, 8f, 15.9f, 8f, 17f)
            verticalLineTo(20f)
            close()
            moveTo(14.825f, 9.825f)
            curveTo(15.608f, 9.042f, 16f, 8.1f, 16f, 7f)
            verticalLineTo(4f)
            horizontalLineTo(8f)
            verticalLineTo(7f)
            curveTo(8f, 8.1f, 8.392f, 9.042f, 9.175f, 9.825f)
            curveTo(9.958f, 10.608f, 10.9f, 11f, 12f, 11f)
            curveTo(13.1f, 11f, 14.042f, 10.608f, 14.825f, 9.825f)
            close()
            moveTo(4f, 22f)
            verticalLineTo(20f)
            horizontalLineTo(6f)
            verticalLineTo(17f)
            curveTo(6f, 15.983f, 6.238f, 15.029f, 6.713f, 14.137f)
            curveTo(7.188f, 13.246f, 7.85f, 12.533f, 8.7f, 12f)
            curveTo(7.85f, 11.467f, 7.188f, 10.754f, 6.713f, 9.863f)
            curveTo(6.238f, 8.971f, 6f, 8.017f, 6f, 7f)
            verticalLineTo(4f)
            horizontalLineTo(4f)
            verticalLineTo(2f)
            horizontalLineTo(20f)
            verticalLineTo(4f)
            horizontalLineTo(18f)
            verticalLineTo(7f)
            curveTo(18f, 8.017f, 17.763f, 8.971f, 17.288f, 9.863f)
            curveTo(16.813f, 10.754f, 16.15f, 11.467f, 15.3f, 12f)
            curveTo(16.15f, 12.533f, 16.813f, 13.246f, 17.288f, 14.137f)
            curveTo(17.763f, 15.029f, 18f, 15.983f, 18f, 17f)
            verticalLineTo(20f)
            horizontalLineTo(20f)
            verticalLineTo(22f)
            horizontalLineTo(4f)
            close()
        }
    }.build()
}
