package ir.ornix.passgen.passwordgenerator.model

import ir.ornix.passgen.codec.Base64BinaryCodec
import ir.ornix.passgen.codec.HexBinaryCodec
import ir.ornix.passgen.codec.Z85BinaryCodec
import ir.ornix.passgen.hashing.core.Hashing
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * Base class for all encoder codecs.
 * Each subclass defines a unique [key] and can create its corresponding [Encoder][ir.ornix.passgen.codec.core.Encoder] instance.
 */
@Serializable(with = PassEncoderSerializer::class)
sealed class PassEncoder(
    override val key: String,
    val binaryBlockSize: Int,
    val encodedBlockSize: Int
) : KeyBasedType<ir.ornix.passgen.codec.core.Encoder>() {

    fun encode(input: ByteArray): String {
        val size = input.size - (input.size % binaryBlockSize)
        return createInstance().encode(input.copyOfRange(0, size))
    }

    object HexPassEncoder : PassEncoder(KEY_HEX_PASS_ENCODER, 1, 2) {
        override fun createInstance() = HexBinaryCodec(false)
    }

    object Base64PassEncoder : PassEncoder(KEY_BASE64_PASS_ENCODER, 3, 4) {
        override fun createInstance() = Base64BinaryCodec()
    }

    object Z85PassEncoder : PassEncoder(KEY_Z85_PASS_ENCODER, 4, 5) {
        override fun createInstance() = Z85BinaryCodec()
    }

    companion object {

        const val KEY_HEX_PASS_ENCODER = "HEX"
        const val KEY_BASE64_PASS_ENCODER = "BASE64"
        const val KEY_Z85_PASS_ENCODER = "Z85"

        val items by lazy {
            listOf(
                HexPassEncoder,
                Base64PassEncoder,
                Z85PassEncoder
            )
        }

        fun fromKey(key: String): PassEncoder = when (key) {
            KEY_HEX_PASS_ENCODER -> HexPassEncoder
            KEY_BASE64_PASS_ENCODER -> Base64PassEncoder
            KEY_Z85_PASS_ENCODER -> Z85PassEncoder
            else -> throw IllegalArgumentException("Unknown encoder key: $key")
        }
    }


    /**
     * The number of encoded units required to represent
     * the output of the given [hashing] algorithm after applying this PassEncoder
     *
     * The hash output size is fixed and defined by the hashing algorithm itself.
     * This method delegates the size calculation to the encoder, which may
     * introduce expansion or padding depending on its encoding granularity.
     *
     * @param hashing Hashing algorithm that produces a fixed-length byte output.
     * encoded representation.
     * @return Number of encoded units needed for the encoded hash output.
     */
    fun getTokenLength(hashing: Hashing): Int {

        /** Size of the raw hash output produced by the hashing algorithm, in bytes.*/
        val tokenByteSize = hashing.outputByteSize

        return (tokenByteSize / binaryBlockSize) * encodedBlockSize
    }


    // decodedApproximateByteCount
    fun approximateDecodedSize(encodedLength: Int): Int {
        return (encodedLength * binaryBlockSize) / encodedBlockSize
    }
}

object PassEncoderSerializer : KSerializer<PassEncoder> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("PassEncoder", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PassEncoder) =
        encoder.encodeString(value.key)

    override fun deserialize(decoder: Decoder): PassEncoder =
        PassEncoder.fromKey(decoder.decodeString())
}
