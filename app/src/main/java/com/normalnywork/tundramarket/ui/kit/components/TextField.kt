package com.normalnywork.tundramarket.ui.kit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMShapes

@Composable
fun TMTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: ImageVector? = null,
    placeholder: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    multiline: Boolean = false,
    height: Dp = TextFieldTokens.DefaultHeight,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    inputTransformation: InputTransformation = InputTransformation,
    outputTransformation: OutputTransformation? = null,
    colors: TMTextFieldColors = TMTextFieldColors.Default,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val strokeColor by colors.strokeColor(focused = focused)

    Column(
        verticalArrangement = Arrangement.spacedBy(TextFieldTokens.LabelSpacing),
        modifier = modifier,
    ) {
        if (label != null) {
            Label(
                label = label,
                icon = icon,
                colors = colors,
            )
        }
        BasicTextField(
            state = state,
            textStyle = LocalTMTypography.current.body.copy(color = colors.textColor),
            cursorBrush = SolidColor(colors.cursorColor),
            lineLimits = if (multiline) {
                TextFieldLineLimits.MultiLine()
            } else {
                TextFieldLineLimits.SingleLine
            },
            interactionSource = interactionSource,
            keyboardOptions = keyboardOptions,
            onKeyboardAction = onKeyboardAction,
            inputTransformation = inputTransformation,
            outputTransformation = outputTransformation,
            modifier = Modifier.fillMaxWidth(),
            decorator = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                        .background(
                            color = colors.backgroundColor,
                            shape = TextFieldTokens.Shape,
                        )
                        .border(
                            width = 1.dp,
                            color = strokeColor,
                            shape = TextFieldTokens.Shape,
                        )
                        .padding(horizontal = TextFieldTokens.HorizontalPadding)
                        .then(if (multiline) Modifier.padding(vertical = TextFieldTokens.VerticalPadding) else Modifier),
                    horizontalArrangement = Arrangement.spacedBy(TextFieldTokens.TrailingSpacing),
                    verticalAlignment = if (multiline) Alignment.Top else Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (placeholder != null && state.text.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = LocalTMTypography.current.body,
                                color = colors.placeholderColor,
                                maxLines = if (multiline) Int.MAX_VALUE else 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        innerTextField()
                    }
                    trailingContent?.invoke()
                }
            }
        )
    }
}

@Composable
private fun Label(
    label: String,
    icon: ImageVector?,
    colors: TMTextFieldColors,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(TextFieldTokens.LabelSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.iconColor,
                modifier = Modifier.size(TextFieldTokens.IconSize),
            )
        }
        Text(
            text = label,
            style = LocalTMTypography.current.subtitle,
            color = colors.labelColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private object TextFieldTokens {

    val DefaultHeight = 56.dp
    val LabelSpacing = 8.dp
    val IconSize = 20.dp
    val HorizontalPadding = 16.dp
    val VerticalPadding = 12.dp
    val TrailingSpacing = 12.dp
    val Shape = TMShapes.Medium
}

data class TMTextFieldColors(
    val backgroundColor: Color,
    val strokeColor: Color,
    val focusedStrokeColor: Color,
    val labelColor: Color,
    val iconColor: Color,
    val cursorColor: Color,
    val placeholderColor: Color,
    val textColor: Color,
) {

    @Composable
    fun strokeColor(focused: Boolean): State<Color> {
        val color = if (focused) focusedStrokeColor else strokeColor
        return animateColorAsState(color)
    }

    companion object {

        val Default: TMTextFieldColors
            @Composable
            get() = TMTextFieldColors(
                backgroundColor = LocalTMColors.current.backgroundCard,
                strokeColor = LocalTMColors.current.stroke,
                focusedStrokeColor = LocalTMColors.current.primary,
                labelColor = LocalTMColors.current.textPrimary,
                iconColor = LocalTMColors.current.primary,
                cursorColor = LocalTMColors.current.primary,
                placeholderColor = LocalTMColors.current.textSecondary,
                textColor = LocalTMColors.current.textPrimary,
            )
    }
}