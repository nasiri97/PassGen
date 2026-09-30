package ir.ornix.passgen.core.common.passwordgenerator.model

import ir.ornix.passgen.core.common.codec.Base64BinaryCodec
import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import ir.ornix.passgen.core.common.codec.Z85BinaryCodec
import ir.ornix.passgen.core.common.codec.core.Encoder

sealed class StringPassEncoder(
    key: String,
    instance: Encoder,
    val binaryBlockSize: Int,
    val encodedBlockSize: Int
) : PassEncoder(key, instance) {

    object HexPassEncoder : StringPassEncoder(
        key = KEY_PASS_ENCODER_BASE16_HEXADECIMAL,
        instance = HexBinaryCodec(false),
        binaryBlockSize = 1,
        encodedBlockSize = 2
    )

    object Base64PassEncoder : StringPassEncoder(
        key = KEY_PASS_ENCODER_BASE64_STANDARD,
        instance = Base64BinaryCodec(),
        binaryBlockSize = 3,
        encodedBlockSize = 4
    )

    object Z85PassEncoder : StringPassEncoder(
        key = KEY_PASS_ENCODER_BASE85_Z85,
        instance = Z85BinaryCodec(),
        binaryBlockSize = 4,
        encodedBlockSize = 5
    )


    companion object {
        internal val allStringPassEncoders by lazy {
            listOf(
                HexPassEncoder,
                Base64PassEncoder,
                Z85PassEncoder
            )
        }
    }


    override suspend fun encode(input: ByteArray): String {
        val size = input.size - (input.size % binaryBlockSize)
        return instance.encode(input.copyOfRange(0, size))
    }

    suspend fun encodeAllBytes(input: ByteArray): String {
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

    /**
     * @param passwordLength Encoded string length
     */
    fun getEntropyByteSize(passwordLength: Int): Int {
        return (passwordLength * binaryBlockSize) / encodedBlockSize
    }
}