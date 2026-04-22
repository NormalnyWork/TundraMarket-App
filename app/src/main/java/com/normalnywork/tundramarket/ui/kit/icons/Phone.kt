package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.Phone: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Phone",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(19.95f, 21f)
            curveTo(20.25f, 21f, 20.5f, 20.9f, 20.7f, 20.7f)
            curveTo(20.9f, 20.5f, 21f, 20.25f, 21f, 19.95f)
            verticalLineTo(15.9f)
            curveTo(21f, 15.683f, 20.925f, 15.488f, 20.775f, 15.313f)
            curveTo(20.625f, 15.137f, 20.433f, 15.017f, 20.2f, 14.95f)
            lineTo(16.75f, 14.25f)
            curveTo(16.517f, 14.217f, 16.279f, 14.238f, 16.038f, 14.313f)
            curveTo(15.796f, 14.387f, 15.6f, 14.5f, 15.45f, 14.65f)
            lineTo(13.1f, 17f)
            curveTo(12.467f, 16.633f, 11.867f, 16.229f, 11.3f, 15.788f)
            curveTo(10.733f, 15.346f, 10.192f, 14.867f, 9.675f, 14.35f)
            curveTo(9.125f, 13.817f, 8.621f, 13.262f, 8.163f, 12.688f)
            curveTo(7.704f, 12.113f, 7.308f, 11.517f, 6.975f, 10.9f)
            lineTo(9.4f, 8.45f)
            curveTo(9.533f, 8.317f, 9.625f, 8.158f, 9.675f, 7.975f)
            curveTo(9.725f, 7.792f, 9.733f, 7.567f, 9.7f, 7.3f)
            lineTo(9.05f, 3.8f)
            curveTo(9.017f, 3.583f, 8.908f, 3.396f, 8.725f, 3.237f)
            curveTo(8.542f, 3.079f, 8.333f, 3f, 8.1f, 3f)
            horizontalLineTo(4.05f)
            curveTo(3.75f, 3f, 3.5f, 3.1f, 3.3f, 3.3f)
            curveTo(3.1f, 3.5f, 3f, 3.75f, 3f, 4.05f)
            curveTo(3f, 6.133f, 3.454f, 8.192f, 4.363f, 10.225f)
            curveTo(5.271f, 12.258f, 6.558f, 14.108f, 8.225f, 15.775f)
            curveTo(9.892f, 17.442f, 11.742f, 18.729f, 13.775f, 19.638f)
            curveTo(15.808f, 20.546f, 17.867f, 21f, 19.95f, 21f)
            close()
            moveTo(6.05f, 9f)
            curveTo(5.767f, 8.35f, 5.55f, 7.692f, 5.4f, 7.025f)
            curveTo(5.25f, 6.358f, 5.133f, 5.683f, 5.05f, 5f)
            horizontalLineTo(7.25f)
            lineTo(7.7f, 7.35f)
            lineTo(6.05f, 9f)
            close()
            moveTo(15f, 17.9f)
            lineTo(16.65f, 16.25f)
            lineTo(19f, 16.75f)
            verticalLineTo(18.95f)
            curveTo(18.317f, 18.9f, 17.642f, 18.783f, 16.975f, 18.6f)
            curveTo(16.308f, 18.417f, 15.65f, 18.183f, 15f, 17.9f)
            close()
        }
    }.build()
}
