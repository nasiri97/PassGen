package ir.ornix.passgen.core.common.passwordgenerator.model

import ir.ornix.passgen.core.common.codec.Bip39Codec
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder.Bip39L12PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder.Bip39L15PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder.Bip39L18PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder.Bip39L21PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder.Bip39L24PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder.Base64PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder.HexPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder.Z85PassEncoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * Base class for all encoder codecs.
 * Each subclass defines a unique [key] and can create its corresponding [Encoder][ir.ornix.passgen.core.common.codec.core.Encoder] instance.
 */
@Serializable(with = PassEncoderSerializer::class)
sealed class PassEncoder : KeyBasedType<ir.ornix.passgen.core.common.codec.core.Encoder>() {

    abstract fun encode(input: ByteArray): String

    companion object {

        const val KEY_HEX_PASS_ENCODER = "HEX"
        const val KEY_BASE64_PASS_ENCODER = "BASE64"
        const val KEY_Z85_PASS_ENCODER = "Z85"
        const val KEY_BIP39_L12_PASS_ENCODER = "BIP39L12"
        const val KEY_BIP39_L15_PASS_ENCODER = "BIP39L15"
        const val KEY_BIP39_L18_PASS_ENCODER = "BIP39L18"
        const val KEY_BIP39_L21_PASS_ENCODER = "BIP39L21"
        const val KEY_BIP39_L24_PASS_ENCODER = "BIP39L24"

        internal val allItems by lazy {
            listOf(
                HexPassEncoder,
                Base64PassEncoder,
                Z85PassEncoder,
                Bip39L12PassEncoder,
                Bip39L15PassEncoder,
                Bip39L18PassEncoder,
                Bip39L21PassEncoder,
                Bip39L24PassEncoder
            )
        }


        fun getValidItems(inputHasher: InputHasher): List<PassEncoder> {
            return getValidItems(inputHasher.outputByteSize)
        }


        fun getValidItems(entropyByteSize: Int): List<PassEncoder> {
            return allItems.filter { passEncoder ->
                when (passEncoder) {
                    is SeedPassEncoder -> {
                        entropyByteSize >= (passEncoder.instance as Bip39Codec).strength.entropyBytes
                    }

                    is StringPassEncoder -> {
                        true
                    }
                }
            }
        }

        fun fromKey(key: String): PassEncoder = when (key) {
            KEY_HEX_PASS_ENCODER -> HexPassEncoder
            KEY_BASE64_PASS_ENCODER -> Base64PassEncoder
            KEY_Z85_PASS_ENCODER -> Z85PassEncoder
            KEY_BIP39_L12_PASS_ENCODER -> Bip39L12PassEncoder
            KEY_BIP39_L15_PASS_ENCODER -> Bip39L15PassEncoder
            KEY_BIP39_L18_PASS_ENCODER -> Bip39L18PassEncoder
            KEY_BIP39_L21_PASS_ENCODER -> Bip39L21PassEncoder
            KEY_BIP39_L24_PASS_ENCODER -> Bip39L24PassEncoder
            else -> throw IllegalArgumentException("Unknown encoder key: $key")
        }
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