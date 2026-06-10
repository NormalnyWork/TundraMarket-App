package com.normalnywork.tundramarket.ui.kit.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.normalnywork.tundramarket.ui.kit.style.LocalTMColors

@Composable
fun TMTimeline(
    itemsCount: Int,
    modifier: Modifier = Modifier,
    markerSize: Dp = TMTimelineTokens.MarkerSize,
    itemSpacing: Dp = TMTimelineTokens.ItemSpacing,
    contentSpacing: Dp = TMTimelineTokens.ContentSpacing,
    lineColor: Color = LocalTMColors.current.primaryVariant,
    marker: @Composable BoxScope.(index: Int) -> Unit,
    content: @Composable (index: Int) -> Unit,
) {
    Column(modifier = modifier) {
        repeat(itemsCount) { index ->
            TMTimelineItem(
                isLast = index == itemsCount - 1,
                markerSize = markerSize,
                itemSpacing = itemSpacing,
                contentSpacing = contentSpacing,
                lineColor = lineColor,
                marker = { marker(index) },
                content = { content(index) },
            )
        }
    }
}

@Composable
private fun TMTimelineItem(
    isLast: Boolean,
    markerSize: Dp,
    itemSpacing: Dp,
    contentSpacing: Dp,
    lineColor: Color,
    marker: @Composable BoxScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Box(
            modifier = Modifier
                .width(markerSize)
                .heightIn(min = if (isLast) markerSize else markerSize + itemSpacing)
                .fillMaxHeight(),
        ) {
            if (!isLast) {
                DashedTimelineLine(
                    markerSize = markerSize,
                    color = lineColor,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Box(
                modifier = Modifier
                    .size(markerSize)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center,
                content = marker,
            )
        }
        Spacer(modifier = Modifier.width(contentSpacing))
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else itemSpacing),
        ) {
            content()
        }
    }
}

@Composable
private fun DashedTimelineLine(
    markerSize: Dp,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        drawLine(
            color = color,
            start = Offset(x = size.width / 2f, y = markerSize.toPx()),
            end = Offset(x = size.width / 2f, y = size.height),
            strokeWidth = TMTimelineTokens.LineWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(
                    TMTimelineTokens.DashLength.toPx(),
                    TMTimelineTokens.DashGap.toPx(),
                ),
            ),
        )
    }
}

private object TMTimelineTokens {

    val MarkerSize = 40.dp
    val ItemSpacing = 12.dp
    val ContentSpacing = 16.dp
    val LineWidth = 2.dp
    val DashLength = 2.dp
    val DashGap = 2.dp
}
