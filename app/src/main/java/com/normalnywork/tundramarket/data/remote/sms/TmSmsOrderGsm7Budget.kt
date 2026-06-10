package com.normalnywork.tundramarket.data.remote.sms

internal object TmSmsOrderGsm7Budget {

    fun calculate(message: String): Int {
        return message.sumOf { char -> if (char == '~') 2 else 1 }
    }
}
