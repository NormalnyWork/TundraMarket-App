package com.normalnywork.tundramarket.ui.kit.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object TMShapes {

    val Small = RoundedCornerShape(Tokens.SMALL_RADIUS)
    val Medium = RoundedCornerShape(Tokens.MEDIUM_RADIUS)
    val Big = RoundedCornerShape(Tokens.BIG_RADIUS)

    object Tokens {

        val SMALL_RADIUS = 4.dp
        val MEDIUM_RADIUS = 8.dp
        val BIG_RADIUS = 12.dp
    }
}