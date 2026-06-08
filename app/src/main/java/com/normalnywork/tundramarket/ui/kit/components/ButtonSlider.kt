package com.normalnywork.tundramarket.ui.kit.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.ui.kit.icons.ArrowForward
import com.normalnywork.tundramarket.ui.kit.icons.TMIcons
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors
import com.normalnywork.tundramarket.ui.kit.style.LocalTMTypography
import com.normalnywork.tundramarket.ui.kit.style.TMPreviewWrapperProvider
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun TMButtonSlider(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: TMButtonSliderColors = TMButtonSliderColors.Default,
) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    val heightPx = with(density) { ButtonSliderTokens.Height.toPx() }
    val maxPaddingPx = with(density) { ButtonSliderTokens.HandlePadding.toPx() }
    val handleSizePx = heightPx - 2f * maxPaddingPx

    var containerWidthPx by remember { mutableFloatStateOf(0f) }

    val maxDragPx by remember(containerWidthPx) {
        derivedStateOf { (containerWidthPx - handleSizePx).coerceAtLeast(0f) }
    }

    val dragOffset = remember { Animatable(0f) }

    val progress by remember {
        derivedStateOf {
            if (maxDragPx > 0f) {
                (dragOffset.value / maxDragPx).coerceIn(0f, 1f)
            } else {
                0f
            }
        }
    }

    fun animateBack() {
        coroutineScope.launch {
            dragOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            )
        }
    }

    fun triggerShake() {
        coroutineScope.launch {
            val peakPx = (maxDragPx * 0.10f).coerceAtLeast(with(density) { 20.dp.toPx() })
            dragOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 500
                    0f at 0 using LinearEasing
                    peakPx at 90 using FastOutSlowInEasing
                    peakPx * 0.55f at 200 using LinearEasing
                    peakPx at 310 using FastOutSlowInEasing
                    0f at 500 using FastOutSlowInEasing
                },
            )
        }
    }

    val gestureModifier = Modifier
        .pointerInput(maxDragPx) {
            detectHorizontalDragGestures(
                onDragStart = {
                    coroutineScope.launch { dragOffset.stop() }
                },
                onHorizontalDrag = { _, delta ->
                    coroutineScope.launch {
                        dragOffset.snapTo((dragOffset.value + delta).coerceIn(0f, maxDragPx))
                    }
                },
                onDragEnd = {
                    coroutineScope.launch {
                        if (progress >= ButtonSliderTokens.CompletionThreshold) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            dragOffset.animateTo(
                                targetValue = maxDragPx,
                                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                            )
                            onClick()
                            dragOffset.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(stiffness = Spring.StiffnessLow),
                            )
                        } else {
                            animateBack()
                        }
                    }
                },
                onDragCancel = { animateBack() },
            )
        }

    val pad = maxPaddingPx * (1f - progress)
    val filledWidth = with(density) { (handleSizePx + dragOffset.value).toDp() }
    val filledHeight = with(density) { (heightPx - 2f * pad).toDp() }
    val handleSize = with(density) { handleSizePx.toDp() }
    val labelOffset = with(density) { (handleSizePx + maxPaddingPx).toDp() }
    val arrowAlpha = if (progress < 0.8f) 1f else ((1f - progress) / 0.2f).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .height(ButtonSliderTokens.Height)
            .clip(ButtonSliderTokens.Shape)
            .background(colors.backgroundColor)
            .onSizeChanged { containerWidthPx = it.width.toFloat() }
            .pointerInput(Unit) { detectTapGestures(onTap = { triggerShake() }) },
    ) {
        Box(
            modifier = Modifier
                .padding(start = labelOffset)
                .padding(horizontal = ButtonSliderTokens.ContentPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text.uppercase(),
                style = LocalTMTypography.current.button,
                color = colors.textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(x = pad.roundToInt(), y = pad.roundToInt()) }
                .width(filledWidth)
                .height(filledHeight)
                .clip(ButtonSliderTokens.Shape)
                .background(colors.handleColor),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Box(
                modifier = Modifier
                    .size(handleSize)
                    .then(gestureModifier),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = TMIcons.ArrowForward,
                    contentDescription = null,
                    tint = colors.iconColor.copy(alpha = arrowAlpha),
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

private object ButtonSliderTokens {

    val Height = 64.dp
    val HandlePadding = 4.dp
    val ContentPadding = 12.dp
    const val CompletionThreshold = 0.95f
    val Shape = CircleShape
}

data class TMButtonSliderColors(
    val backgroundColor: Color,
    val handleColor: Color,
    val textColor: Color,
    val iconColor: Color,
) {

    companion object {

        val Default: TMButtonSliderColors
            @Composable
            get() = TMButtonSliderColors(
                backgroundColor = LocalTMColors.current.primaryVariant,
                handleColor = LocalTMColors.current.primary,
                textColor = LocalTMColors.current.primary,
                iconColor = LocalTMColors.current.onPrimary,
            )
    }
}

@PreviewWrapper(TMPreviewWrapperProvider::class)
@Preview
@Composable
private fun PreviewDefault() {
    TMButtonSlider(
        text = "Slide",
        onClick = {},
    )
}
