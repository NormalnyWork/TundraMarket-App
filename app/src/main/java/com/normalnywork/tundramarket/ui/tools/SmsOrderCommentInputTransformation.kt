package com.normalnywork.tundramarket.ui.tools

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.delete

val SmsOrderCommentInputTransformation = InputTransformation {
    var index = 0
    while (index < length) {
        if (charAt(index).isAllowedSmsOrderCommentChar()) {
            index++
        } else {
            delete(index, index + 1)
        }
    }
}

private fun Char.isAllowedSmsOrderCommentChar(): Boolean {
    return this == ',' ||
        this == ' ' ||
        isDigit() ||
        lowercaseChar() in AllowedRussianLetters
}

private val AllowedRussianLetters = setOf(
    'а',
    'б',
    'в',
    'г',
    'д',
    'е',
    'ё',
    'ж',
    'з',
    'и',
    'й',
    'к',
    'л',
    'м',
    'н',
    'о',
    'п',
    'р',
    'с',
    'т',
    'у',
    'ф',
    'х',
    'ц',
    'ч',
    'ш',
    'щ',
    'ъ',
    'ы',
    'ь',
    'э',
    'ю',
    'я',
)
