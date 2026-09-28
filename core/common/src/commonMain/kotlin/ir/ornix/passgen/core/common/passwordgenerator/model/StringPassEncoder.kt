package ir.ornix.passgen.core.common.passwordgenerator.model

import ir.ornix.passgen.core.common.codec.Base64BinaryCodec
import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import ir.ornix.passgen.core.common.codec.Z85BinaryCodec

sealed class StringPassEncoder(
    override val key: String,
    val binaryBlockSize: Int,
    val encodedBlockSize: Int
) : PassEncoder() {

    object HexPassEncoder : StringPassEncoder(KEY_HEX_PASS_ENCODER, 1, 2) {
        override val instance = HexBinaryCodec(false)
    }

    object Base64PassEncoder : StringPassEncoder(KEY_BASE64_PASS_ENCODER, 3, 4) {
        override val instance = Base64BinaryCodec()
    }

    object Z85PassEncoder : StringPassEncoder(KEY_Z85_PASS_ENCODER, 4, 5) {
        override val instance = Z85BinaryCodec()
    }


    override fun encode(input: ByteArray): String {
        val size = input.size - (input.size % binaryBlockSize)
        return instance.encode(input.copyOfRange(0, size))
    }

    fun encodeAllBytes(input: ByteArray): String {
        return instance.encode(input)
    }

    /**
     * Returns the number of encoded units required to represent the given number
     * of input bytes using this encoder.
     *
     * The calculation is based on the encoder's binary block size and encoded
     * block size. Each binary block of [binaryBlockSize] bytes is represented by
     * [encodedBlockSize] encoded units.
     *
     * @param byteSize Number of input bytes to encode.
     * @return Number of encoded units required to represent the input.
     */
    fun getTokenLength(byteSize: Int): Int {
        return (byteSize / binaryBlockSize) * encodedBlockSize
    }

    /** decodedApproximateByteCount */
    fun approximateDecodedSize(encodedLength: Int): Int {
        return (encodedLength * binaryBlockSize) / encodedBlockSize
    }
}