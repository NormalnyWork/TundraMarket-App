package com.normalnywork.tundramarket.ui.kit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TMIcons.Location: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Location",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color(0xFFD01039))) {
            moveTo(13.413f, 11.413f)
            curveTo(13.804f, 11.021f, 14f, 10.55f, 14f, 10f)
            curveTo(14f, 9.45f, 13.804f, 8.979f, 13.413f, 8.587f)
            curveTo(13.021f, 8.196f, 12.55f, 8f, 12f, 8f)
            curveTo(11.45f, 8f, 10.979f, 8.196f, 10.587f, 8.587f)
            curveTo(10.196f, 8.979f, 10f, 9.45f, 10f, 10f)
            curveTo(10f, 10.55f, 10.196f, 11.021f, 10.587f, 11.413f)
            curveTo(10.979f, 11.804f, 11.45f, 12f, 12f, 12f)
            curveTo(12.55f, 12f, 13.021f, 11.804f, 13.413f, 11.413f)
            close()
            moveTo(12f, 19.35f)
            curveTo(14.033f, 17.483f, 15.542f, 15.788f, 16.525f, 14.262f)
            curveTo(17.508f, 12.738f, 18f, 11.383f, 18f, 10.2f)
            curveTo(18f, 8.383f, 17.421f, 6.896f, 16.263f, 5.738f)
            curveTo(15.104f, 4.579f, 13.683f, 4f, 12f, 4f)
            curveTo(10.317f, 4f, 8.896f, 4.579f, 7.738f, 5.738f)
            curveTo(6.579f, 6.896f, 6f, 8.383f, 6f, 10.2f)
            curveTo(6f, 11.383f, 6.492f, 12.738f, 7.475f, 14.262f)
            curveTo(8.458f, 15.788f, 9.967f, 17.483f, 12f, 19.35f)
            close()
            moveTo(12f, 22f)
            curveTo(9.317f, 19.717f, 7.313f, 17.596f, 5.988f, 15.637f)
            curveTo(4.662f, 13.679f, 4f, 11.867f, 4f, 10.2f)
            curveTo(4f, 7.7f, 4.804f, 5.708f, 6.412f, 4.225f)
            curveTo(8.021f, 2.742f, 9.883f, 2f, 12f, 2f)
            curveTo(14.117f, 2f, 15.979f, 2.742f, 17.587f, 4.225f)
            curveTo(19.196f, 5.708f, 20f, 7.7f, 20f, 10.2f)
            curveTo(20f, 11.867f, 19.337f, 13.679f, 18.013f, 15.637f)
            curveTo(16.688f, 17.596f, 14.683f, 19.717f, 12f, 22f)
            close()
        }
    }.build()
}
