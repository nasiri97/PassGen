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
     * The number of encoded units required to represent
     * the output of the given [inputHasher] algorithm after applying this PassEncoder
     *
     * The hash output size is fixed and defined by the hashing algorithm itself.
     * This method delegates the size calculation to the encoder, which may
     * introduce expansion or padding depending on its encoding granularity.
     *
     * @param inputHasher Hashing algorithm that produces a fixed-length byte output.
     * encoded representation.
     * @return Number of encoded units needed for the encoded hash output.
     */
    fun getTokenLength(inputHasher: InputHasher): Int {

        /** Size of the raw hash output produced by the hashing algorithm, in bytes.*/
        val tokenByteSize = inputHasher.outputByteSize

        return (tokenByteSize / binaryBlockSize) * encodedBlockSize
    }


    // decodedApproximateByteCount
    fun approximateDecodedSize(encodedLength: Int): Int {
        return (encodedLength * binaryBlockSize) / encodedBlockSize
    }
}