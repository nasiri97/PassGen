package ir.ornix.passgen.codec

import ir.ornix.passgen.codec.core.Codec

/**
 * A [Codec] implementation for hexadecimal (Base16) encoding.
 *
 * ### How it works
 *
 * Each byte maps to exactly two hex characters (0-9, A-F), so unlike
 * Base64-style codecs there is no partial-block or padding logic to worry
 * about.
 *
 * ### Size Relationship (1 byte → 2 characters)
 *
 * Every input byte independently becomes 2 output characters,
 * and the encoded string length is always exactly `input.size * 2`.
 *
 * So:
 * - Input byte count       -> can be any length (0, 1, 2, 3, 4, 5, ...)
 * - Output string length   -> always a multiple of 2 (0, 2, 4, ...)
 *
 * @property uppercaseOutput controls the letter casing used by [encode]
 * (`"FF"` vs `"ff"`). [decode] accepts either case, and mixed case,
 * regardless of this setting — it only affects the encoder's output.
 *
 * @throws IllegalArgumentException if [decode] is given a string with an
 * odd length or containing non-hex characters.
 */
class HexBinaryCodec(val uppercaseOutput: Boolean) : Codec {

    companion object {
        private const val BLOCK_SIZE: Int = 1
        private const val ENCODED_BLOCK_SIZE = 2

        private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()
    }

    override fun decode(input: String): ByteArray {
        require(input.length % ENCODED_BLOCK_SIZE == 0) {
            "Input length (${input.length}) must be a multiple of encoded block size ($ENCODED_BLOCK_SIZE)"
        }

        return ByteArray(input.length / 2) { i ->
            val index = i * 2
            input.substring(index, index + 2).toInt(16).toByte()
        }
    }

    override fun encode(input: ByteArray): String {
        require(input.size % BLOCK_SIZE == 0) {
            "Input size (${input.size}) must be a multiple of block size ($BLOCK_SIZE)"
        }

        val hexChars = CharArray(input.size * 2)
        for (j in input.indices) {
            val v = input[j].toInt() and 0xFF
            hexChars[j * 2] = HEX_ARRAY[v ushr 4]
            hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
        }
        val resultHex = hexChars.concatToString()

        return if (uppercaseOutput) resultHex.uppercase()
        else resultHex.lowercase()
    }
}