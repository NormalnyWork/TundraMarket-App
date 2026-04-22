package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.Waiting: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Waiting",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(15.3f, 16.7f)
            lineTo(16.7f, 15.3f)
            lineTo(13f, 11.6f)
            verticalLineTo(7f)
            horizontalLineTo(11f)
            verticalLineTo(12.4f)
            lineTo(15.3f, 16.7f)
            close()
            moveTo(12f, 22f)
            curveTo(10.617f, 22f, 9.317f, 21.737f, 8.1f, 21.212f)
            curveTo(6.883f, 20.688f, 5.825f, 19.975f, 4.925f, 19.075f)
            curveTo(4.025f, 18.175f, 3.313f, 17.117f, 2.787f, 15.9f)
            curveTo(2.263f, 14.683f, 2f, 13.383f, 2f, 12f)
            curveTo(2f, 10.617f, 2.263f, 9.317f, 2.787f, 8.1f)
            curveTo(3.313f, 6.883f, 4.025f, 5.825f, 4.925f, 4.925f)
            curveTo(5.825f, 4.025f, 6.883f, 3.313f, 8.1f, 2.787f)
            curveTo(9.317f, 2.263f, 10.617f, 2f, 12f, 2f)
            curveTo(13.383f, 2f, 14.683f, 2.263f, 15.9f, 2.787f)
            curveTo(17.117f, 3.313f, 18.175f, 4.025f, 19.075f, 4.925f)
            curveTo(19.975f, 5.825f, 20.688f, 6.883f, 21.212f, 8.1f)
            curveTo(21.737f, 9.317f, 22f, 10.617f, 22f, 12f)
            curveTo(22f, 13.383f, 21.737f, 14.683f, 21.212f, 15.9f)
            curveTo(20.688f, 17.117f, 19.975f, 18.175f, 19.075f, 19.075f)
            curveTo(18.175f, 19.975f, 17.117f, 20.688f, 15.9f, 21.212f)
            curveTo(14.683f, 21.737f, 13.383f, 22f, 12f, 22f)
            close()
            moveTo(12f, 20f)
            curveTo(14.217f, 20f, 16.104f, 19.221f, 17.663f, 17.663f)
            curveTo(19.221f, 16.104f, 20f, 14.217f, 20f, 12f)
            curveTo(20f, 9.783f, 19.221f, 7.896f, 17.663f, 6.338f)
            curveTo(16.104f, 4.779f, 14.217f, 4f, 12f, 4f)
            curveTo(9.783f, 4f, 7.896f, 4.779f, 6.338f, 6.338f)
            curveTo(4.779f, 7.896f, 4f, 9.783f, 4f, 12f)
            curveTo(4f, 14.217f, 4.779f, 16.104f, 6.338f, 17.663f)
            curveTo(7.896f, 19.221f, 9.783f, 20f, 12f, 20f)
            close()
        }
    }.build()
}
