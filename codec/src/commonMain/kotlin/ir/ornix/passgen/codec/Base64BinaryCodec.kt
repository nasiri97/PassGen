package ir.ornix.passgen.codec


import ir.ornix.passgen.codec.core.FixedLengthCodec
import kotlin.io.encoding.Base64

class Base64BinaryCodec : FixedLengthCodec {

    override val blockSize: Int = 3
    override val encodedBlockSize = 4

    override fun decodeInternal(input: String): ByteArray {
        return Base64.decode(input)
    }

    override fun encodeInternal(input: ByteArray): String {
        return Base64.encode(input)
    }
}