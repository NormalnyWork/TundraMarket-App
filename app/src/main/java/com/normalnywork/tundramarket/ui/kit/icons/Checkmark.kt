package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.Checkmark: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Checkmark",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(10.6f, 16.6f)
            lineTo(17.65f, 9.55f)
            lineTo(16.25f, 8.15f)
            lineTo(10.6f, 13.8f)
            lineTo(7.75f, 10.95f)
            lineTo(6.35f, 12.35f)
            lineTo(10.6f, 16.6f)
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
            curveTo(14.233f, 20f, 16.125f, 19.225f, 17.675f, 17.675f)
            curveTo(19.225f, 16.125f, 20f, 14.233f, 20f, 12f)
            curveTo(20f, 9.767f, 19.225f, 7.875f, 17.675f, 6.325f)
            curveTo(16.125f, 4.775f, 14.233f, 4f, 12f, 4f)
            curveTo(9.767f, 4f, 7.875f, 4.775f, 6.325f, 6.325f)
            curveTo(4.775f, 7.875f, 4f, 9.767f, 4f, 12f)
            curveTo(4f, 14.233f, 4.775f, 16.125f, 6.325f, 17.675f)
            curveTo(7.875f, 19.225f, 9.767f, 20f, 12f, 20f)
            close()
        }
    }.build()
}
