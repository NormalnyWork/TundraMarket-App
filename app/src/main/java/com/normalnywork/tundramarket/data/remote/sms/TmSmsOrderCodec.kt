package com.normalnywork.tundramarket.data.remote.sms

interface TmSmsOrderCodec {

    val version: Int

    fun encode(order: TmSmsOrder): TmSmsOrderEncodeResult

    fun decode(message: String): TmSmsOrderDecodeResult

    fun isEncodedOrderMessage(message: String): Boolean

    fun validate(message: String): TmSmsOrderValidationResult

    fun canFitInSingleSms(order: TmSmsOrder): Boolean

    fun canFitInSingleSms(message: String): Boolean

    fun calculateSmsLength(message: String): Int
}

sealed interface TmSmsOrderEncodeResult {

    data class Success(val message: String) : TmSmsOrderEncodeResult

    data class Failure(val errors: List<TmSmsOrderValidationError>) : TmSmsOrderEncodeResult
}

sealed interface TmSmsOrderDecodeResult {

    data class Success(
        val order: TmSmsOrder,
        val version: Int,
    ) : TmSmsOrderDecodeResult

    data class Failure(val errors: List<TmSmsOrderValidationError>) : TmSmsOrderDecodeResult
}

data class TmSmsOrderValidationResult(
    val isValid: Boolean,
    val errors: List<TmSmsOrderValidationError>,
    val smsLength: Int,
    val codec: TmSmsOrderCodec?,
) {

    companion object {

        fun valid(
            smsLength: Int,
            codec: TmSmsOrderCodec,
        ) = TmSmsOrderValidationResult(
            isValid = true,
            errors = emptyList(),
            smsLength = smsLength,
            codec = codec,
        )

        fun invalid(
            errors: List<TmSmsOrderValidationError>,
            smsLength: Int,
        ) = TmSmsOrderValidationResult(
            isValid = false,
            errors = errors,
            smsLength = smsLength,
            codec = null,
        )
    }
}

enum class TmSmsOrderValidationError {
    EmptyMessage,
    InvalidProtocolMarker,
    InvalidPartsCount,
    UnsupportedVersion,
    InvalidVersion,
    InvalidClientOrderId,
    InvalidLocation,
    InvalidCart,
    InvalidCrc,
    InvalidComment,
    SmsLengthExceeded,
}
