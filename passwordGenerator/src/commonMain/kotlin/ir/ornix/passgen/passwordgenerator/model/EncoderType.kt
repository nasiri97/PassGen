package ir.ornix.passgen.passwordgenerator.model

import ir.ornix.passgen.codec.Base64BinaryCodec
import ir.ornix.passgen.codec.HexBinaryCodec
import ir.ornix.passgen.codec.Z85BinaryCodec
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * Base class for all encoder codecs.
 * Each subclass defines a unique [key] and can create its corresponding [Encoder][ir.ornix.passgen.core.codec.core.Encoder] instance.
 */
@Serializable(with = EncoderTypeSerializer::class)
sealed class EncoderType(override val key: String) : KeyBasedType<FixedLengthEncoder>() {

    object HEX : EncoderType(KEY_HEX) {
        override fun createInstance() = HexBinaryCodec(false)
    }

    object BASE64 : EncoderType(KEY_BASE64) {
        override fun createInstance() = Base64BinaryCodec()
    }

    object Z85 : EncoderType(KEY_Z85) {
        override fun createInstance() = Z85BinaryCodec()
    }

    companion object {

        const val KEY_HEX = "HEX"
        const val KEY_BASE64 = "BASE64"
        const val KEY_Z85 = "Z85"

        val items by lazy {
            listOf(
                HEX,
                BASE64,
                Z85
            )
        }

        fun fromKey(key: String): EncoderType = when (key) {
            KEY_HEX -> HEX
            KEY_BASE64 -> BASE64
            KEY_Z85 -> Z85
            else -> throw IllegalArgumentException("Unknown encoder key: $key")
        }
    }
}

object EncoderTypeSerializer : KSerializer<EncoderType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EncoderType", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: EncoderType) = encoder.encodeString(value.key)
    override fun deserialize(decoder: Decoder): EncoderType = EncoderType.fromKey(decoder.decodeString())
}
