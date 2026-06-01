package com.normalnywork.tundramarket.ui.kit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import com.normalnywork.tundramarket.ui.kit.style.TMShapes

@Composable
fun StageBar(
    range: IntRange,
    current: Int,
    modifier: Modifier = Modifier,
    updateStage: ((newStage: Int) -> Unit)? = null,
    indicatorColors: StageIndicatorColors = StageIndicatorColors.Default,
    indicatorShape: StageIndicatorShape = StageIndicatorShape.Default,
) {
    require(current in range) { "Unknown stage: $current" }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = StageBarTokens.Spacing,
            alignment = Alignment.CenterHorizontally
        ),
    ) {
        range.forEach { stage ->
            StageIndicator(
                stage = stage,
                selected = current == stage,
                onClick = updateStage?.let {
                    { updateStage(stage) }
                }.takeIf { stage <= current + 1 },
                colors = indicatorColors,
                shape = indicatorShape,
            )
        }
    }
}

@Composable
private fun StageIndicator(
    stage: Int,
    selected: Boolean,
    onClick: (() -> Unit)? = null,
    colors: StageIndicatorColors = StageIndicatorColors.Default,
    shape: StageIndicatorShape = StageIndicatorShape.Default,
) {
    val roundness by shape.roundness(selected = selected)
    val backgroundColor by colors.backgroundColor(selected = selected)
    val contentColor by colors.contentColor(selected = selected)

    Box(
        modifier = Modifier
            .size(StageBarTokens.IndicatorSize)
            .clip(RoundedCornerShape(roundness))
            .background(color = backgroundColor)
            .clickable(
                enabled = onClick != null && !selected,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = colors.ripple),
                onClick = { onClick?.invoke() }
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$stage",
            style = LocalTMTypography.current.body,
            color = contentColor,
        )
    }
}

private object StageBarTokens {

    val Spacing = 12.dp

    val IndicatorSize = 32.dp
}

data class StageIndicatorColors(
    val backgroundColor: Color,
    val selectedBackgroundColor: Color,
    val contentColor: Color,
    val selectedContentColor: Color,
    val ripple: Color,
) {

    @Composable
    fun backgroundColor(selected: Boolean): State<Color> {
        val color = if (selected) selectedBackgroundColor else backgroundColor
        return animateColorAsState(color)
    }

    @Composable
    fun contentColor(selected: Boolean): State<Color> {
        val color = if (selected) selectedContentColor else contentColor
        return animateColorAsState(color)
    }

    companion object {

        val Default: StageIndicatorColors
            @Composable
            get() = StageIndicatorColors(
                backgroundColor = LocalTMColors.current.backgroundCard,
                selectedBackgroundColor = LocalTMColors.current.primary,
                contentColor = LocalTMColors.current.textSecondary,
                selectedContentColor = LocalTMColors.current.onPrimary,
                ripple = LocalTMColors.current.primaryVariant,
            )
    }
}

data class StageIndicatorShape(
    val roundness: Dp,
    val selectedRoundness: Dp,
) {

    @Composable
    fun roundness(selected: Boolean): State<Dp> {
        val radius = if (selected) selectedRoundness else roundness
        return animateDpAsState(radius)
    }

    companion object {

        val Default: StageIndicatorShape
            @Composable
            get() = StageIndicatorShape(
                roundness = StageBarTokens.IndicatorSize / 2,
                selectedRoundness = TMShapes.Tokens.SMALL_RADIUS,
            )
    }
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun Preview() {
    StageBar(
        range = 1..4,
        current = 2,
    )
}
