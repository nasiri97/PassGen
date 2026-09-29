package ir.ornix.passgen.core.common.passwordgenerator.model

import ir.ornix.passgen.core.common.codec.Bip39Codec
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder.Bip39L12PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder.Bip39L15PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder.Bip39L18PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder.Bip39L21PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder.Bip39L24PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder.Companion.allSeedPassEncoders
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder.Base64PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder.Companion.allStringPassEncoders
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

    abstract suspend fun encode(input: ByteArray): String

    val shortName
        get() = when (key) {
            KEY_PASS_ENCODER_BASE16_HEXADECIMAL -> "HEX"
            KEY_PASS_ENCODER_BASE64_STANDARD -> "BASE64"
            KEY_PASS_ENCODER_BASE85_Z85 -> "Z85"
            KEY_PASS_ENCODER_BIP39_12_WORDS -> "BIP39L12"
            KEY_PASS_ENCODER_BIP39_15_WORDS -> "BIP39L15"
            KEY_PASS_ENCODER_BIP39_18_WORDS -> "BIP39L18"
            KEY_PASS_ENCODER_BIP39_21_WORDS -> "BIP39L21"
            KEY_PASS_ENCODER_BIP39_24_WORDS -> "BIP39L24"
            else -> throw IllegalArgumentException("Unknown encoder key: $key")
        }

    val fullName
        get() = when (key) {
            KEY_PASS_ENCODER_BASE16_HEXADECIMAL -> "Base16 (Hexadecimal)"
            KEY_PASS_ENCODER_BASE64_STANDARD -> "Base64 (Standard)"
            KEY_PASS_ENCODER_BASE85_Z85 -> "Base85 (Z85)"
            KEY_PASS_ENCODER_BIP39_12_WORDS -> "BIP39 (12 words)"
            KEY_PASS_ENCODER_BIP39_15_WORDS -> "BIP39 (15 words)"
            KEY_PASS_ENCODER_BIP39_18_WORDS -> "BIP39 (18 words)"
            KEY_PASS_ENCODER_BIP39_21_WORDS -> "BIP39 (21 words)"
            KEY_PASS_ENCODER_BIP39_24_WORDS -> "BIP39 (24 words)"
            else -> throw IllegalArgumentException("Unknown encoder key: $key")
        }

    companion object {

        const val KEY_PASS_ENCODER_BASE16_HEXADECIMAL = "PASS_ENCODER_BASE16_HEXADECIMAL"
        const val KEY_PASS_ENCODER_BASE64_STANDARD = "PASS_ENCODER_BASE64_STANDARD"
        const val KEY_PASS_ENCODER_BASE85_Z85 = "PASS_ENCODER_BASE85_Z85"
        const val KEY_PASS_ENCODER_BIP39_12_WORDS = "PASS_ENCODER_BIP39_12_WORDS"
        const val KEY_PASS_ENCODER_BIP39_15_WORDS = "PASS_ENCODER_BIP39_15_WORDS"
        const val KEY_PASS_ENCODER_BIP39_18_WORDS = "PASS_ENCODER_BIP39_18_WORDS"
        const val KEY_PASS_ENCODER_BIP39_21_WORDS = "PASS_ENCODER_BIP39_21_WORDS"
        const val KEY_PASS_ENCODER_BIP39_24_WORDS = "PASS_ENCODER_BIP39_24_WORDS"

        internal val allItems by lazy {
            allStringPassEncoders + allSeedPassEncoders
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
            KEY_PASS_ENCODER_BASE16_HEXADECIMAL -> HexPassEncoder
            KEY_PASS_ENCODER_BASE64_STANDARD -> Base64PassEncoder
            KEY_PASS_ENCODER_BASE85_Z85 -> Z85PassEncoder
            KEY_PASS_ENCODER_BIP39_12_WORDS -> Bip39L12PassEncoder
            KEY_PASS_ENCODER_BIP39_15_WORDS -> Bip39L15PassEncoder
            KEY_PASS_ENCODER_BIP39_18_WORDS -> Bip39L18PassEncoder
            KEY_PASS_ENCODER_BIP39_21_WORDS -> Bip39L21PassEncoder
            KEY_PASS_ENCODER_BIP39_24_WORDS -> Bip39L24PassEncoder
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