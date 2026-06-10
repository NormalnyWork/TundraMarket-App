package com.normalnywork.tundramarket.data.remote.sms

object TmSmsOrderCodecs {

    val actualCodec: TmSmsOrderCodec = TmSmsOrderCodecV1

    val singleSmsLimit: Int = TmSmsOrderConstants.SmsLimit

    private val codecsByVersion = listOf(actualCodec).associateBy { codec -> codec.version }

    fun encode(order: TmSmsOrder): TmSmsOrderEncodeResult {
        return actualCodec.encode(order)
    }

    fun calculateSmsLength(order: TmSmsOrder): TmSmsOrderLengthResult {
        return actualCodec.calculateSmsLength(order)
    }

    fun canFitInSingleSms(order: TmSmsOrder): Boolean {
        return actualCodec.canFitInSingleSms(order)
    }

    fun validate(message: String): TmSmsOrderValidationResult {
        val smsLength = calculateSmsLength(message)
        val routingResult = route(message)
        if (routingResult.errors.isNotEmpty()) {
            return TmSmsOrderValidationResult.invalid(
                errors = routingResult.errors,
                smsLength = smsLength,
            )
        }

        return routingResult.codec?.validate(message)
            ?: TmSmsOrderValidationResult.invalid(
                errors = listOf(TmSmsOrderValidationError.UnsupportedVersion),
                smsLength = smsLength,
            )
    }

    fun isEncodedOrderMessage(message: String): Boolean {
        return message.substringBefore('.') == TmSmsOrderConstants.ProtocolMarker
    }

    fun calculateSmsLength(message: String): Int {
        return TmSmsOrderGsm7Budget.calculate(message)
    }

    private fun route(message: String): RoutingResult {
        val errors = mutableListOf<TmSmsOrderValidationError>()
        if (message.isEmpty()) {
            errors += TmSmsOrderValidationError.EmptyMessage
        }

        val parts = message.split('.', limit = 3)
        if (parts.getOrNull(ProtocolIndex) != TmSmsOrderConstants.ProtocolMarker) {
            errors += TmSmsOrderValidationError.InvalidProtocolMarker
        }

        val version = parts.getOrNull(VersionIndex)
            ?.takeIf { versionText -> versionText.length == 1 }
            ?.let { versionText -> TmSmsOrderBase62.decodeInt(versionText).getOrNull() }
        if (version == null || version <= 0) {
            errors += TmSmsOrderValidationError.InvalidVersion
        }

        if (errors.isNotEmpty()) {
            return RoutingResult(errors = errors.distinct())
        }

        val codec = codecsByVersion[version]
        return RoutingResult(
            codec = codec,
            errors = if (codec == null) {
                listOf(TmSmsOrderValidationError.UnsupportedVersion)
            } else {
                emptyList()
            },
        )
    }

    private data class RoutingResult(
        val codec: TmSmsOrderCodec? = null,
        val errors: List<TmSmsOrderValidationError> = emptyList(),
    )

    private const val ProtocolIndex = 0
    private const val VersionIndex = 1
}
