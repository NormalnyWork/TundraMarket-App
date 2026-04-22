package com.normalnywork.tundramarket.ui.kit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.icons.UserLocation
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMShapes
import com.normalnywork.tundramarket.ui.kit.style.TundraMarketTheme

@Composable
fun TMButtonPrimary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    colors: TMButtonPrimaryColors = TMButtonPrimaryColors.Default,
) {
    val backgroundColor by colors.backgroundColor(enabled = enabled)
    val contentColor by colors.contentColor(enabled = enabled)

    Row(
        modifier = modifier
            .height(ButtonPrimaryTokens.Height)
            .clip(ButtonPrimaryTokens.Shape)
            .background(color = backgroundColor)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = colors.rippleColor),
                onClick = onClick,
            )
            .padding(horizontal = ButtonPrimaryTokens.PaddingHorizontal),
        horizontalArrangement = Arrangement.spacedBy(ButtonPrimaryTokens.Spacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(ButtonPrimaryTokens.IconSize),
            )
        }
        Text(
            text = text.uppercase(),
            style = LocalTMTypography.current.button,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}

private object ButtonPrimaryTokens {

    val Height = 56.dp
    val IconSize = 24.dp
    val PaddingHorizontal = 32.dp
    val Spacing = 16.dp
    val Shape = TMShapes.Medium
}

data class TMButtonPrimaryColors(
    val backgroundColor: Color,
    val disabledBackgroundColor: Color,
    val contentColor: Color,
    val disabledContentColor: Color,
    val rippleColor: Color,
) {

    @Composable
    fun backgroundColor(enabled: Boolean): State<Color> {
        val color = if (enabled) backgroundColor else disabledBackgroundColor
        return animateColorAsState(color)
    }

    @Composable
    fun contentColor(enabled: Boolean): State<Color> {
        val color = if (enabled) contentColor else disabledContentColor
        return animateColorAsState(color)
    }
    
    companion object {
        
        val Default: TMButtonPrimaryColors
            @Composable
            get() = TMButtonPrimaryColors(
                backgroundColor = LocalTMColors.current.primary,
                disabledBackgroundColor = LocalTMColors.current.primary.copy(alpha = 0.5f),
                contentColor = LocalTMColors.current.onPrimary,
                disabledContentColor = LocalTMColors.current.onPrimary,
                rippleColor = LocalTMColors.current.onPrimary,
            )
    }
}

@Preview
@Composable
private fun PreviewDefault() {
    TundraMarketTheme {
        TMButtonPrimary(
            text = "Default",
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewIcon() {
    TundraMarketTheme {
        TMButtonPrimary(
            text = "Icon",
            icon = TMIcons.UserLocation,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewDisabled() {
    TundraMarketTheme {
        TMButtonPrimary(
            text = "Icon",
            onClick = {},
            enabled = false,
        )
    }
}