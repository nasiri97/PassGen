package ir.ornix.passgen.codec

import ir.ornix.passgen.codec.core.FixedLengthCodec

class HexBinaryCodec constructor(
    val uppercaseOutput: Boolean
) : FixedLengthCodec {

    companion object {
        private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()
    }

    override val blockSize: Int = 1
    override val encodedBlockSize = 2

    override fun decode(input: String): ByteArray {
        require(input.length % 2 == 0) { "Hex string must have even length" }

        return ByteArray(input.length / 2) { i ->
            val index = i * 2
            input.substring(index, index + 2).toInt(16).toByte()
        }
    }

    override fun encode(input: ByteArray): String {
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