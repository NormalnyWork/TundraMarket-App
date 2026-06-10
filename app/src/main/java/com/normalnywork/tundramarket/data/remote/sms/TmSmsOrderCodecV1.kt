package com.normalnywork.tundramarket.data.remote.sms

import com.normalnywork.tundramarket.domain.entities.Location
import kotlin.math.roundToLong

object TmSmsOrderCodecV1 : TmSmsOrderCodec {

    override val version: Int = 1

    override fun encode(order: TmSmsOrder): TmSmsOrderEncodeResult {
        val message = when (val result = buildMessage(order)) {
            is BuildMessageResult.Success -> result.message
            is BuildMessageResult.Failure -> return TmSmsOrderEncodeResult.Failure(result.errors)
        }

        if (calculateSmsLength(message) > TmSmsOrderConstants.SmsLimit) {
            return TmSmsOrderEncodeResult.Failure(listOf(TmSmsOrderValidationError.SmsLengthExceeded))
        }

        return TmSmsOrderEncodeResult.Success(message)
    }

    override fun decode(message: String): TmSmsOrderDecodeResult {
        val validationResult = validate(message)
        if (!validationResult.isValid) {
            return TmSmsOrderDecodeResult.Failure(validationResult.errors)
        }

        val parts = message.split('.')
        return TmSmsOrderDecodeResult.Success(
            order = TmSmsOrder(
                clientOrderId = TmSmsOrderBase62.decodeInt(parts[ClientOrderIdIndex]).getOrThrow(),
                location = decodeLocation(parts[LocationIndex]),
                cart = decodeCart(parts[CartIndex]),
                comment = parts.getOrNull(CommentIndex)?.let { encodedComment ->
                    TmSmsOrderCommentCodec.decode(encodedComment).getOrThrow()
                },
            ),
            version = version,
        )
    }

    override fun isEncodedOrderMessage(message: String): Boolean {
        val parts = message.split('.', limit = 3)
        val messageVersion = parts.getOrNull(VersionIndex)
            ?.let { versionText -> TmSmsOrderBase62.decodeInt(versionText).getOrNull() }

        return parts.getOrNull(ProtocolIndex) == TmSmsOrderConstants.ProtocolMarker && messageVersion == version
    }

    override fun validate(message: String): TmSmsOrderValidationResult {
        val smsLength = calculateSmsLength(message)
        val errors = mutableListOf<TmSmsOrderValidationError>()
        if (message.isEmpty()) {
            errors += TmSmsOrderValidationError.EmptyMessage
        }
        if (smsLength > TmSmsOrderConstants.SmsLimit) {
            errors += TmSmsOrderValidationError.SmsLengthExceeded
        }

        val parts = message.split('.')
        if (parts.size != RequiredPartsCount && parts.size != RequiredPartsCountWithComment) {
            errors += TmSmsOrderValidationError.InvalidPartsCount
        }

        if (parts.getOrNull(ProtocolIndex) != TmSmsOrderConstants.ProtocolMarker) {
            errors += TmSmsOrderValidationError.InvalidProtocolMarker
        }
        validateVersion(parts.getOrNull(VersionIndex), errors)
        validateClientOrderId(parts.getOrNull(ClientOrderIdIndex), errors)
        validateLocation(parts.getOrNull(LocationIndex), errors)
        validateCart(parts.getOrNull(CartIndex), errors)
        validateCrc(parts, errors)
        validateComment(parts.getOrNull(CommentIndex), hasComment = parts.size == RequiredPartsCountWithComment, errors)

        return if (errors.isEmpty()) {
            TmSmsOrderValidationResult.valid(
                smsLength = smsLength,
                codec = this,
            )
        } else {
            TmSmsOrderValidationResult.invalid(errors.distinct(), smsLength)
        }
    }

    override fun canFitInSingleSms(order: TmSmsOrder): Boolean {
        val encoded = encode(order)
        return encoded is TmSmsOrderEncodeResult.Success && calculateSmsLength(encoded.message) <= TmSmsOrderConstants.SmsLimit
    }

    override fun canFitInSingleSms(message: String): Boolean {
        return calculateSmsLength(message) <= TmSmsOrderConstants.SmsLimit
    }

    override fun calculateSmsLength(message: String): Int {
        return TmSmsOrderGsm7Budget.calculate(message)
    }

    override fun calculateSmsLength(order: TmSmsOrder): TmSmsOrderLengthResult {
        val message = when (val result = buildMessage(order)) {
            is BuildMessageResult.Success -> result.message
            is BuildMessageResult.Failure -> return TmSmsOrderLengthResult.Failure(result.errors)
        }

        return TmSmsOrderLengthResult.Success(calculateSmsLength(message))
    }

    private fun buildMessage(order: TmSmsOrder): BuildMessageResult {
        val errors = validateOrder(order).toMutableList()
        val encodedComment = order.comment
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.let { comment -> TmSmsOrderCommentCodec.encode(comment).getOrNull() }

        if (!order.comment.isNullOrBlank() && encodedComment == null) {
            errors += TmSmsOrderValidationError.InvalidComment
        }

        if (errors.isNotEmpty()) {
            return BuildMessageResult.Failure(errors.distinct())
        }

        val body = listOf(
            TmSmsOrderConstants.ProtocolMarker,
            TmSmsOrderBase62.encode(version),
            TmSmsOrderBase62.encode(order.clientOrderId),
            encodeLocation(order.location),
            encodeCart(order.cart),
        ).joinToString(separator = ".")

        return BuildMessageResult.Success(
            message = buildString {
                append(body)
                append('.')
                append(encodeCrc(body))

                if (encodedComment != null) {
                    append('.')
                    append(encodedComment)
                }
            }
        )
    }

    private fun validateOrder(order: TmSmsOrder): List<TmSmsOrderValidationError> {
        val errors = mutableListOf<TmSmsOrderValidationError>()
        if (order.clientOrderId <= 0) {
            errors += TmSmsOrderValidationError.InvalidClientOrderId
        }
        if (!isValidLocation(order.location)) {
            errors += TmSmsOrderValidationError.InvalidLocation
        }
        if (order.cart.isEmpty() || order.cart.any { item -> !isValidCartItem(item) }) {
            errors += TmSmsOrderValidationError.InvalidCart
        }

        return errors
    }

    private fun validateVersion(
        versionText: String?,
        errors: MutableList<TmSmsOrderValidationError>,
    ) {
        val decodedVersion = versionText
            ?.takeIf { it.length == 1 }
            ?.let { TmSmsOrderBase62.decodeInt(it).getOrNull() }
        if (decodedVersion == null || decodedVersion <= 0) {
            errors += TmSmsOrderValidationError.InvalidVersion
        } else if (decodedVersion != version) {
            errors += TmSmsOrderValidationError.UnsupportedVersion
        }
    }

    private fun validateClientOrderId(
        clientOrderIdText: String?,
        errors: MutableList<TmSmsOrderValidationError>,
    ) {
        val clientOrderId = clientOrderIdText?.let { TmSmsOrderBase62.decodeInt(it).getOrNull() }
        if (clientOrderId == null || clientOrderId <= 0) {
            errors += TmSmsOrderValidationError.InvalidClientOrderId
        }
    }

    private fun validateLocation(
        locationText: String?,
        errors: MutableList<TmSmsOrderValidationError>,
    ) {
        if (locationText == null || locationText.length != TmSmsOrderConstants.LocationLength) {
            errors += TmSmsOrderValidationError.InvalidLocation
            return
        }

        val latText = locationText.take(TmSmsOrderConstants.LocationPartLength)
        val lonText = locationText.drop(TmSmsOrderConstants.LocationPartLength)
        val latU = TmSmsOrderBase62.decodeLong(latText).getOrNull()
        val lonU = TmSmsOrderBase62.decodeLong(lonText).getOrNull()
        if (
            latU == null ||
            lonU == null ||
            latU !in MinEncodedLatitude..MaxEncodedLatitude ||
            lonU !in MinEncodedLongitude..MaxEncodedLongitude
        ) {
            errors += TmSmsOrderValidationError.InvalidLocation
        }
    }

    private fun validateCart(
        cartText: String?,
        errors: MutableList<TmSmsOrderValidationError>,
    ) {
        if (cartText.isNullOrEmpty() || cartText.length % CartItemLength != 0 || !TmSmsOrderBase62.isBase62(cartText)) {
            errors += TmSmsOrderValidationError.InvalidCart
            return
        }

        val hasInvalidQuantity = cartText
            .chunked(CartItemLength)
            .any { item -> TmSmsOrderBase62.decodeInt(item[QuantityOffset].toString()).getOrNull() == 0 }
        if (hasInvalidQuantity) {
            errors += TmSmsOrderValidationError.InvalidCart
        }
    }

    private fun validateCrc(
        parts: List<String>,
        errors: MutableList<TmSmsOrderValidationError>,
    ) {
        val crcText = parts.getOrNull(CrcIndex)
        if (crcText == null || crcText.length != TmSmsOrderConstants.CrcLength) {
            errors += TmSmsOrderValidationError.InvalidCrc
            return
        }

        val crc = TmSmsOrderBase62.decodeInt(crcText).getOrNull()
        val body = parts.take(CrcIndex).joinToString(separator = ".")
        if (crc == null || crc !in MinCrc..MaxCrc || encodeCrc(body) != crcText) {
            errors += TmSmsOrderValidationError.InvalidCrc
        }
    }

    private fun validateComment(
        commentText: String?,
        hasComment: Boolean,
        errors: MutableList<TmSmsOrderValidationError>,
    ) {
        if (!hasComment) return

        if (commentText.isNullOrEmpty() || TmSmsOrderCommentCodec.decode(commentText).isFailure) {
            errors += TmSmsOrderValidationError.InvalidComment
        }
    }

    private fun encodeLocation(location: Location): String {
        val latU = ((location.latitude.toDouble() + LatitudeOffset) * CoordinatePrecision).roundToLong()
        val lonU = ((location.longitude.toDouble() + LongitudeOffset) * CoordinatePrecision).roundToLong()
        return TmSmsOrderBase62.encode(latU).padStart(TmSmsOrderConstants.LocationPartLength, '0') +
            TmSmsOrderBase62.encode(lonU).padStart(TmSmsOrderConstants.LocationPartLength, '0')
    }

    private fun decodeLocation(locationText: String): Location {
        val latU = TmSmsOrderBase62.decodeLong(locationText.take(TmSmsOrderConstants.LocationPartLength)).getOrThrow()
        val lonU = TmSmsOrderBase62.decodeLong(locationText.drop(TmSmsOrderConstants.LocationPartLength)).getOrThrow()
        return Location(
            latitude = (latU / CoordinatePrecision - LatitudeOffset).toFloat(),
            longitude = (lonU / CoordinatePrecision - LongitudeOffset).toFloat(),
        )
    }

    private fun encodeCart(cart: List<TmSmsOrderCartItem>): String {
        return cart.joinToString(separator = "") { item ->
            TmSmsOrderBase62.encode(item.productId) + TmSmsOrderBase62.encode(item.quantity)
        }
    }

    private fun decodeCart(cartText: String): List<TmSmsOrderCartItem> {
        return cartText.chunked(CartItemLength).map { item ->
            TmSmsOrderCartItem(
                productId = TmSmsOrderBase62.decodeInt(item[ProductIdOffset].toString()).getOrThrow(),
                quantity = TmSmsOrderBase62.decodeInt(item[QuantityOffset].toString()).getOrThrow(),
            )
        }
    }

    private fun encodeCrc(body: String): String {
        return TmSmsOrderBase62.encode(TmSmsOrderCrc16.calculate(body)).padStart(TmSmsOrderConstants.CrcLength, '0')
    }

    private fun isValidLocation(location: Location): Boolean {
        return !location.latitude.isNaN() &&
            !location.latitude.isInfinite() &&
            !location.longitude.isNaN() &&
            !location.longitude.isInfinite() &&
            location.latitude in -LatitudeOffset..LatitudeOffset &&
            location.longitude in -LongitudeOffset..LongitudeOffset
    }

    private fun isValidCartItem(item: TmSmsOrderCartItem): Boolean {
        return item.productId in MinSingleCharValue..TmSmsOrderConstants.MaxBase62SingleCharValue &&
            item.quantity in MinQuantity..TmSmsOrderConstants.MaxBase62SingleCharValue
    }

    private const val ProtocolIndex = 0
    private const val VersionIndex = 1
    private const val ClientOrderIdIndex = 2
    private const val LocationIndex = 3
    private const val CartIndex = 4
    private const val CrcIndex = 5
    private const val CommentIndex = 6
    private const val RequiredPartsCount = 6
    private const val RequiredPartsCountWithComment = 7
    private const val CartItemLength = 2
    private const val ProductIdOffset = 0
    private const val QuantityOffset = 1
    private const val MinSingleCharValue = 0
    private const val MinQuantity = 1
    private const val MinCrc = 0
    private const val MaxCrc = 65535
    private const val CoordinatePrecision = 100_000.0
    private const val LatitudeOffset = 90.0
    private const val LongitudeOffset = 180.0
    private const val MinEncodedLatitude = 0L
    private const val MaxEncodedLatitude = 18_000_000L
    private const val MinEncodedLongitude = 0L
    private const val MaxEncodedLongitude = 36_000_000L

    private sealed interface BuildMessageResult {

        data class Success(val message: String) : BuildMessageResult

        data class Failure(val errors: List<TmSmsOrderValidationError>) : BuildMessageResult
    }
}
