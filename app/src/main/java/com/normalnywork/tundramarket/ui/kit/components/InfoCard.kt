package com.normalnywork.tundramarket.ui.kit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.Waiting
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.kit.style.TMShapes

@Composable
fun InfoCard(
    title: String,
    body: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = LocalTMColors.current.secondary,
                shape = TMShapes.Medium,
            )
            .background(
                color = LocalTMColors.current.secondaryVariant,
                shape = TMShapes.Medium,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LocalTMColors.current.primary,
            modifier = Modifier.size(24.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title.uppercase(),
                style = LocalTMTypography.current.label,
                color = LocalTMColors.current.textPrimary,
            )
            Text(
                text = body,
                style = LocalTMTypography.current.body,
                color = LocalTMColors.current.textSecondary,
            )
        }
    }
}

@Composable
fun ExtendedInfoCard(
    caption: String,
    title: String,
    body: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = LocalTMColors.current.secondary,
                shape = TMShapes.Medium,
            )
            .background(
                color = LocalTMColors.current.secondaryVariant,
                shape = TMShapes.Medium,
            )
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LocalTMColors.current.primary,
                modifier = Modifier.size(24.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = caption.uppercase(),
                    style = LocalTMTypography.current.captionLabel,
                    color = LocalTMColors.current.textSecondary,
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = title.uppercase(),
                        style = LocalTMTypography.current.label,
                        color = LocalTMColors.current.textPrimary,
                    )
                    Text(
                        text = body,
                        style = LocalTMTypography.current.body,
                        color = LocalTMColors.current.textSecondary,
                    )
                }
            }
        }
        action?.invoke()
    }
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun Preview() {
    InfoCard(
        title = "Title",
        body = "Body",
        icon = TMIcons.Waiting,
    )
}
