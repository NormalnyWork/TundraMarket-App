package com.normalnywork.tundramarket.data.remote.sms

internal object TmSmsOrderBase62 {

    private const val Alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val Base = Alphabet.length

    fun encode(value: Int): String = encode(value.toLong())

    fun encode(value: Long): String {
        require(value >= 0) { "Base62 value must be non-negative" }
        if (value == 0L) return "0"

        var current = value
        val result = StringBuilder()
        while (current > 0L) {
            result.append(Alphabet[(current % Base).toInt()])
            current /= Base
        }

        return result.reverse().toString()
    }

    fun decodeInt(text: String): Result<Int> {
        return decodeLong(text).mapCatching { value ->
            if (value > Int.MAX_VALUE) error("Base62 value does not fit Int")
            value.toInt()
        }
    }

    fun decodeLong(text: String): Result<Long> = runCatching {
        require(text.isNotEmpty()) { "Base62 value must not be empty" }

        var result = 0L
        text.forEach { char ->
            val digit = Alphabet.indexOf(char)
            require(digit >= 0) { "Invalid Base62 character" }
            check(result <= (Long.MAX_VALUE - digit) / Base) { "Base62 value does not fit Long" }
            result = result * Base + digit
        }

        result
    }

    fun isBase62(text: String): Boolean {
        return text.isNotEmpty() && text.all { char -> Alphabet.indexOf(char) >= 0 }
    }
}
