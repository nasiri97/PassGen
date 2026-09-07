package ir.ornix.passgen.codec

import ir.ornix.passgen.codec.BCryptBase64BinaryCodec.Companion.ALPHABET
import ir.ornix.passgen.codec.BCryptBase64BinaryCodec.Companion.DECODING
import ir.ornix.passgen.codec.core.Codec


/**
 * A [Codec] implementation for BCrypt's custom Base64 variant.
 *
 * BCrypt does not use standard Base64 (RFC 4648). It uses its own alphabet
 * ("./A-Za-z0-9" instead of "A-Za-z0-9+/") and omits padding characters ('=').
 * This codec implements that specific variant so binary data (e.g. salts and
 * hash output) can be safely represented as text in BCrypt-compatible formats.
 *
 * ### How encoding works
 * Input bytes are processed in groups of up to 3 bytes (24 bits), which are
 * split into 4 groups of 6 bits each. Each 6-bit value (0-63) is mapped to
 * one character in [ALPHABET]. If the final group has fewer than 3 bytes,
 * the missing bytes are treated as zero for bit-packing purposes, and the
 * output is truncated accordingly (no '=' padding is appended).
 *
 * ### How decoding works
 * The reverse process: each character is mapped back to its 6-bit value via
 * [DECODING] (a lookup table built from [ALPHABET]), and every 4 characters
 * are recombined into 3 bytes. Trailing partial groups are handled the same
 * way as in encoding — producing 1 or 2 bytes as appropriate.
 *
 * ### Size Relationship (3 bytes → 4 characters)
 *
 * Unlike standard Base64, a BCrypt Base64-encoded string is NOT always a
 * multiple of 4 in length, because this variant omits padding entirely —
 * there is no '=' character in its alphabet.
 *
 * Encoding still processes bytes in groups of 3, and a full group still
 * produces exactly 4 characters. But when the input's final group has only
 * 1 or 2 bytes, the output is simply truncated instead of being padded out
 * to the next 4-character boundary:
 *
 * | Bytes in final group | Output chars |
 * |---|---|
 * | 3 (full block) | 4 |
 * | 2              | 3 |
 * | 1              | 2 |
 *
 * So:
 * - Input byte count     -> can be any length (0, 1, 2, 3, 4, 5, ...)
 * - Output string length -> follows ceil(n * 4 / 3), NOT necessarily a
 *   multiple of 4
 *
 * This is the key structural difference from RFC 4648 Base64: padding
 * exists there to preserve a fixed 4-char boundary; BCrypt's variant has
 * no such guarantee since it drops padding altogether.
 *
 * @throws IllegalArgumentException if [decode] encounters a character outside
 * the BCrypt Base64 alphabet.
 */
class BCryptBase64BinaryCodec : Codec {

    companion object {
        private const val ALPHABET =
            "./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

        private val DECODING = IntArray(128) { -1 }.apply {
            ALPHABET.forEachIndexed { index, char ->
                this[char.code] = index
            }
        }
    }


    override fun encode(input: ByteArray): String {
        val length: Int = input.size
        val result = StringBuilder()

        var offset = 0

        while (offset < length) {
            val c1 = input[offset].toInt() and 0xFF
            offset++

            result.append(ALPHABET[c1 shr 2])

            var c2 = 0
            if (offset < length) {
                c2 = input[offset].toInt() and 0xFF
            }

            result.append(
                ALPHABET[((c1 and 0x03) shl 4) or (c2 shr 4)]
            )

            if (offset >= length) {
                break
            }

            offset++

            var c3 = 0
            if (offset < length) {
                c3 = input[offset].toInt() and 0xFF
            }

            result.append(
                ALPHABET[((c2 and 0x0F) shl 2) or (c3 shr 6)]
            )

            if (offset >= length) {
                break
            }

            offset++

            result.append(
                ALPHABET[c3 and 0x3F]
            )
        }

        return result.toString()
    }

    override fun decode(input: String): ByteArray {
        val length: Int = input.length
        val result = ByteArray(length * 3 / 4 + 1)

        var inputOffset = 0
        var outputOffset = 0

        while (
            inputOffset < length &&
            outputOffset < result.size
        ) {
            val c1 = charValue(input[inputOffset++])

            if (inputOffset >= length) {
                break
            }

            val c2 = charValue(input[inputOffset++])

            result[outputOffset++] =
                ((c1 shl 2) or (c2 shr 4)).toByte()

            if (inputOffset >= length) {
                break
            }

            val c3 = charValue(input[inputOffset++])

            result[outputOffset++] =
                (((c2 and 0x0F) shl 4) or (c3 shr 2)).toByte()

            if (inputOffset >= length) {
                break
            }

            val c4 = charValue(input[inputOffset++])

            result[outputOffset++] =
                (((c3 and 0x03) shl 6) or c4).toByte()
        }

        return result.copyOf(outputOffset)
    }

    private fun charValue(char: Char): Int {
        val code = char.code

        require(code < DECODING.size) {
            "Invalid BCrypt Base64 character: $char"
        }

        val value = DECODING[code]

        require(value >= 0) {
            "Invalid BCrypt Base64 character: $char"
        }

        return value
    }
}