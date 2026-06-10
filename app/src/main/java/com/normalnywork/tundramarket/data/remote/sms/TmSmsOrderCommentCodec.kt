package com.normalnywork.tundramarket.data.remote.sms

internal object TmSmsOrderCommentCodec {

    private val simpleLetters = mapOf(
        'а' to 'a',
        'б' to 'b',
        'в' to 'v',
        'г' to 'g',
        'д' to 'd',
        'е' to 'e',
        'з' to 'z',
        'и' to 'i',
        'й' to 'j',
        'к' to 'k',
        'л' to 'l',
        'м' to 'm',
        'н' to 'n',
        'о' to 'o',
        'п' to 'p',
        'р' to 'r',
        'с' to 's',
        'т' to 't',
        'у' to 'u',
        'ф' to 'f',
        'х' to 'h',
        'ц' to 'c',
        'ы' to 'y',
        'э' to 'q',
    )

    private val complexLetters = mapOf(
        'ж' to '1',
        'ч' to '2',
        'ш' to '3',
        'щ' to '4',
        'ю' to '5',
        'я' to '6',
        'ё' to '7',
        'ь' to '8',
        'ъ' to '9',
    )

    private val decodedSimpleLetters = simpleLetters.entries.associate { (decoded, encoded) -> encoded to decoded }
    private val decodedComplexLetters = complexLetters.entries.associate { (decoded, encoded) -> encoded to decoded }

    fun encode(comment: String): Result<String> = runCatching {
        val result = StringBuilder()
        var index = 0
        while (index < comment.length) {
            val char = comment[index].lowercaseChar()
            when {
                char.isDigit() -> {
                    result.append('~')
                    while (index < comment.length && comment[index].isDigit()) {
                        result.append(comment[index])
                        index++
                    }
                    continue
                }
                char == ',' || char == ' ' -> result.append(char)
                simpleLetters.containsKey(char) -> result.append(simpleLetters.getValue(char))
                complexLetters.containsKey(char) -> result.append(complexLetters.getValue(char))
                else -> error("Unsupported comment character")
            }
            index++
        }

        result.toString()
    }

    fun decode(comment: String): Result<String> = runCatching {
        val result = StringBuilder()
        var index = 0
        while (index < comment.length) {
            val char = comment[index]
            when {
                char == '~' -> {
                    index++
                    require(index < comment.length && comment[index].isDigit()) { "Invalid escaped number" }
                    while (index < comment.length && comment[index].isDigit()) {
                        result.append(comment[index])
                        index++
                    }
                    continue
                }
                char == ',' || char == ' ' -> result.append(char)
                char.isDigit() -> result.append(decodedComplexLetters[char] ?: error("Invalid comment digit"))
                decodedSimpleLetters.containsKey(char) -> result.append(decodedSimpleLetters.getValue(char))
                else -> error("Unsupported encoded comment character")
            }
            index++
        }

        result.toString()
    }
}
