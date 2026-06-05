package com.normalnywork.tundramarket.ui.tools

import java.util.Locale

fun Float?.toDisplayCoordinate(): String {
    if (this == null) return ""

    return String.format(
        locale = Locale.US,
        format = "%.${DECIMAL_PLACES}f",
        this,
    )
}

private const val DECIMAL_PLACES = 5
