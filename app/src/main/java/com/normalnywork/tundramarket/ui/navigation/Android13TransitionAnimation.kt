package com.normalnywork.tundramarket.ui.navigation

import android.annotation.SuppressLint
import android.graphics.Path
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.core.view.animation.PathInterpolatorCompat
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.Direction
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalDecomposeApi::class)
@SuppressLint("ComposableNaming")
fun android13NavigationTransition(
    animationSpec: FiniteAnimationSpec<Float> = tween(
        durationMillis = 450,
        easing = FastOutExtraSlowInInterpolator()
    ),
    coefficient: Int = 10,
) = stackAnimator(animationSpec = animationSpec) { factor, direction ->
    val alpha = getFadeAlpha(factor = factor)
    val offsetX = getOffsetX(factor = factor, direction = direction, coefficient = coefficient)

    Modifier
        .graphicsLayer {
            this.alpha = alpha
        }
        .offset { offsetX }
}

private fun getFadeAlpha(factor: Float): Float {
    return (1F - abs(factor)).coerceIn(minimumValue = 0F, maximumValue = 1F)
}

private fun getOffsetX(factor: Float, direction: Direction, coefficient: Int): IntOffset {
    val offsetValue = (factor * 1000 / coefficient).roundToInt()
    return when (direction) {
        Direction.ENTER_FRONT -> IntOffset(x = offsetValue, y = 0)
        Direction.EXIT_BACK -> IntOffset(x = offsetValue, y = 0)
        Direction.EXIT_FRONT -> IntOffset(x = offsetValue, y = 0)
        Direction.ENTER_BACK -> IntOffset(x = offsetValue, y = 0)
    }
}

private class FastOutExtraSlowInInterpolator : Easing {
    private val fastOutExtraSlowInInterpolator =
        PathInterpolatorCompat.create(Path().apply {
            moveTo(0f, 0f)
            cubicTo(0.05f, 0f, 0.133333f, 0.06f, 0.166666f, 0.4f)
            cubicTo(0.208333f, 0.82f, 0.25f, 1f, 1f, 1f)
        })

    override fun transform(fraction: Float): Float {
        return fastOutExtraSlowInInterpolator.getInterpolation(fraction)
    }
}