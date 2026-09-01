package ir.ornix.passgen.codec

import ir.ornix.passgen.codec.core.FixedLengthCodec

class Z85BinaryCodec : FixedLengthCodec {

    override val blockSize: Int = 4
    override val encodedBlockSize: Int = 5

    private companion object {
        const val ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-:+=^!/*?&<>()[]{}@%$#"
    }

    override fun decode(input: String): ByteArray {
        require(input.length % encodedBlockSize == 0) {
            "Base85 string length must be a multiple of $encodedBlockSize because Base85 encodes data in $blockSize-byte blocks."
        }

        val output = ByteArray(input.length / encodedBlockSize * blockSize)
        var outputIndex = 0

        for (i in input.indices step encodedBlockSize) {
            var value = 0L

            for (j in 0 until encodedBlockSize) {
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
        // Fixed: Must be a multiple of blockSize (4), not encodedBlockSize (5)
        require(input.size % blockSize == 0) {
            "Base85 input size must be a multiple of $blockSize bytes."
        }

        val result = StringBuilder(input.size / blockSize * encodedBlockSize)

        for (i in input.indices step blockSize) {
            val value =
                ((input[i].toLong() and 0xFF) shl 24) or
                        ((input[i + 1].toLong() and 0xFF) shl 16) or
                        ((input[i + 2].toLong() and 0xFF) shl 8) or
                        (input[i + 3].toLong() and 0xFF)

            var divisor = 85L * 85 * 85 * 85

            repeat(encodedBlockSize) {
                val digit = ((value / divisor) % 85).toInt()
                result.append(ALPHABET[digit])
                divisor /= 85
            }
        }

        return result.toString()
    }
}