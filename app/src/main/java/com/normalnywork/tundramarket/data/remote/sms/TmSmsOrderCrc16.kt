package com.normalnywork.tundramarket.data.remote.sms

internal object TmSmsOrderCrc16 {

    private const val InitialValue = 0xFFFF
    private const val Polynomial = 0x1021
    private const val ByteMask = 0xFF
    private const val CrcMask = 0xFFFF
    private const val TopBit = 0x8000

    fun calculate(text: String): Int {
        var crc = InitialValue
        text.encodeToByteArray().forEach { byte ->
            crc = crc xor ((byte.toInt() and ByteMask) shl 8)
            repeat(8) {
                crc = if ((crc and TopBit) != 0) {
                    (crc shl 1) xor Polynomial
                } else {
                    crc shl 1
                }
                crc = crc and CrcMask
            }
        }

        return crc and CrcMask
    }
}
