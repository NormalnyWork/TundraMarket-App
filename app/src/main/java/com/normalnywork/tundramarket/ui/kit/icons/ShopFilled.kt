package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.ShopFilled: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "ShopFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(4.8f, 6.6f)
            verticalLineTo(4.8f)
            horizontalLineTo(19.2f)
            verticalLineTo(6.6f)
            horizontalLineTo(4.8f)
            close()
            moveTo(4.8f, 19.8f)
            verticalLineTo(14.4f)
            horizontalLineTo(3.6f)
            verticalLineTo(12.6f)
            lineTo(4.8f, 7.8f)
            horizontalLineTo(19.2f)
            lineTo(20.4f, 12.6f)
            verticalLineTo(14.4f)
            horizontalLineTo(19.2f)
            verticalLineTo(19.8f)
            horizontalLineTo(17.4f)
            verticalLineTo(14.4f)
            horizontalLineTo(13.2f)
            verticalLineTo(19.8f)
            horizontalLineTo(4.8f)
            close()
            moveTo(6.6f, 18f)
            horizontalLineTo(11.4f)
            verticalLineTo(14.4f)
            horizontalLineTo(6.6f)
            verticalLineTo(18f)
            close()
        }
    }.build()
}
