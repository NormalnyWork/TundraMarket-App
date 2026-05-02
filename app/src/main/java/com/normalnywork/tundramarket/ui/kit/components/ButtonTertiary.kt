package com.normalnywork.tundramarket.ui.kit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.UserLocation
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.kit.style.TMShapes

@Composable
fun TMButtonTertiary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    colors: TMButtonTertiaryColors = TMButtonTertiaryColors.Default,
) {
    val strokeColor by colors.strokeColor(enabled = enabled)
    val contentColor by colors.contentColor(enabled = enabled)

    Row(
        modifier = modifier
            .height(ButtonTertiaryTokens.Height)
            .border(
                width = 1.dp,
                shape = ButtonTertiaryTokens.Shape,
                color = strokeColor,
            )
            .clip(ButtonTertiaryTokens.Shape)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = ButtonTertiaryTokens.PaddingHorizontal),
        horizontalArrangement = Arrangement.spacedBy(ButtonTertiaryTokens.Spacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(ButtonTertiaryTokens.IconSize),
            )
        }
        Text(
            text = text.uppercase(),
            style = LocalTMTypography.current.buttonSmall,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}

private object ButtonTertiaryTokens {

    val Height = 56.dp
    val IconSize = 24.dp
    val PaddingHorizontal = 16.dp
    val Spacing = 12.dp
    val Shape = TMShapes.Medium
}

data class TMButtonTertiaryColors(
    val strokeColor: Color,
    val disabledStrokeColor: Color,
    val contentColor: Color,
    val disabledContentColor: Color,
    val rippleColor: Color,
) {

    @Composable
    fun strokeColor(enabled: Boolean): State<Color> {
        val color = if (enabled) strokeColor else disabledStrokeColor
        return animateColorAsState(color)
    }

    @Composable
    fun contentColor(enabled: Boolean): State<Color> {
        val color = if (enabled) contentColor else disabledContentColor
        return animateColorAsState(color)
    }
    
    companion object {
        
        val Default: TMButtonTertiaryColors
            @Composable
            get() = TMButtonTertiaryColors(
                strokeColor = LocalTMColors.current.stroke,
                disabledStrokeColor = LocalTMColors.current.stroke.copy(alpha = 0.5f),
                contentColor = LocalTMColors.current.primary,
                disabledContentColor = LocalTMColors.current.primary.copy(alpha = 0.5f),
                rippleColor = LocalTMColors.current.primaryVariant,
            )
    }
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun PreviewDefault() {
    TMButtonTertiary(
        text = "Default",
        onClick = {},
    )
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun PreviewIcon() {
    TMButtonTertiary(
        text = "Icon",
        icon = TMIcons.UserLocation,
        onClick = {},
    )
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun PreviewDisabled() {
    TMButtonTertiary(
        text = "Icon",
        onClick = {},
        enabled = false,
    )
}
