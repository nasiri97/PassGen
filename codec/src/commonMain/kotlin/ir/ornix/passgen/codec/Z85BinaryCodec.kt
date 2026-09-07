package ir.ornix.passgen.codec

import ir.ornix.passgen.codec.core.Codec


/**
 * A [Codec] implementation for Z85, ZeroMQ's binary-to-text encoding
 * (as specified in [RFC 32](https://rfc.zeromq.org/spec/32/)).
 *
 * Z85 uses an 85-character alphabet
 * to represent binary data more compactly than Base64, at the cost of a
 * stricter structural requirement: it has no padding scheme, so it only
 * accepts input whose length is an exact multiple of 4 bytes.
 *
 * ### How it works
 * Input bytes are processed in fixed groups of 4 (32 bits). Each 32-bit
 * group is treated as a single big-endian unsigned integer and converted
 * to base 85, producing exactly 5 characters per group — chosen because
 * `85^5` (~4.437 billion) is just large enough to represent every possible
 * 32-bit value (`256^4` = ~4.295 billion). Decoding reverses this: every
 * 5 characters are read back as a base-85 number and re-expanded into the
 * original 4 bytes.
 *
 * ### Size Relationship (4 bytes → 5 characters)
 * Unlike Base64, there is no partial-block or padding logic — the mapping
 * is fixed and exact:
 *
 * | Input (bytes) | Output (chars) |
 * |---|---|
 * | 4  | 5  |
 * | 8  | 10 |
 * | 12 | 15 |
 *
 * So:
 * - Input byte count       -> always a multiple of 4 (0, 4, 8, ...)
 * - Output string length   -> always a multiple of 5 (0, 5, 10, ...)
 *
 * @throws IllegalArgumentException if [encode] is given a byte array whose
 * length is not a multiple of 4, or if [decode] is given a string whose
 * length is not a multiple of 5 or that contains characters outside the
 * Z85 alphabet.
 */
class Z85BinaryCodec : Codec {


    private companion object {
        private const val BLOCK_SIZE: Int = 4
        private const val ENCODED_BLOCK_SIZE: Int = 5

        const val ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-:+=^!/*?&<>()[]{}@%$#"
    }

    override fun decode(input: String): ByteArray {
        require(input.length % ENCODED_BLOCK_SIZE == 0) {
            "Input length (${input.length}) must be a multiple of encoded block size ($ENCODED_BLOCK_SIZE)"
        }

        val output = ByteArray(input.length / ENCODED_BLOCK_SIZE * BLOCK_SIZE)
        var outputIndex = 0

        for (i in input.indices step ENCODED_BLOCK_SIZE) {
            var value = 0L

            for (j in 0 until ENCODED_BLOCK_SIZE) {
                val char = input[i + j]
                val digit = ALPHABET.indexOf(char)

                require(digit >= 0) {
                    "Invalid Base85 character: '$char'"
                }

                value = value * 85 + digit
            }

            require(value <= 0xFFFF_FFFFL) {
                "Invalid Base85 block value exceeds 32-bit limit."
            }

            output[outputIndex++] = (value shr 24).toByte()
            output[outputIndex++] = (value shr 16).toByte()
            output[outputIndex++] = (value shr 8).toByte()
            output[outputIndex++] = value.toByte()
        }

        return output
    }

    override fun encode(input: ByteArray): String {
        require(input.size % BLOCK_SIZE == 0) {
            "Input size (${input.size}) must be a multiple of block size ($BLOCK_SIZE)"
        }

        val result = StringBuilder(input.size / BLOCK_SIZE * ENCODED_BLOCK_SIZE)

        for (i in input.indices step BLOCK_SIZE) {
            val value =
                ((input[i].toLong() and 0xFF) shl 24) or
                        ((input[i + 1].toLong() and 0xFF) shl 16) or
                        ((input[i + 2].toLong() and 0xFF) shl 8) or
                        (input[i + 3].toLong() and 0xFF)

            var divisor = 85L * 85 * 85 * 85

            repeat(ENCODED_BLOCK_SIZE) {
                val digit = ((value / divisor) % 85).toInt()
                result.append(ALPHABET[digit])
                divisor /= 85
            }
        }

        return result.toString()
    }
}