package com.normalnywork.tundramarket.ui.tools

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.delete

private const val MaxCoordinateLength = 12

val CoordinateInputTransformation = InputTransformation {
    var separatorSeen = false
    var index = 0

    while (index < length) {
        val char = charAt(index)
        val shouldDelete = when {
            char.isDigit() -> false
            (char == '.' || char == ',') && index != 0 && !separatorSeen -> {
                separatorSeen = true
                false
            }
            else -> true
        }

        if (shouldDelete) {
            delete(index, index + 1)
        } else {
            index++
        }
    }

    if (length > MaxCoordinateLength) {
        delete(MaxCoordinateLength, length)
    }
}
