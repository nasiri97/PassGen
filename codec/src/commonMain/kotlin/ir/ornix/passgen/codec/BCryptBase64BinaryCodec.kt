package ir.ornix.passgen.codec

import ir.ornix.passgen.codec.core.Codec

class BCryptBase64BinaryCodec : Codec {

//    override val blockSize: Int = 3
//    override val encodedBlockSize = 4

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