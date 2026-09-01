package ir.ornix.passgen.codec


import ir.ornix.passgen.codec.core.FixedLengthCodec
import kotlin.io.encoding.Base64

class Base64BinaryCodec : FixedLengthCodec {

    override val blockSize: Int = 3
    override val encodedBlockSize = 4

    override fun decode(input: String): ByteArray {
        require(input.length % 4 == 0) {
            "Base64 string length must be a multiple of 4 because Base64 encodes data in 3-byte blocks, which are represented as 4 characters each."
        }

        return Base64.decode(input)
    }

    override fun encode(input: ByteArray): String {
        return Base64.encode(input)
    }
}