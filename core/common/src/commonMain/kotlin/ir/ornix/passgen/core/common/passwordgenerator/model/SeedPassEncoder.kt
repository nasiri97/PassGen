package ir.ornix.passgen.core.common.passwordgenerator.model

import ir.ornix.passgen.core.common.codec.Bip39Codec
import ir.ornix.passgen.core.common.codec.Bip39Codec.Strength

sealed class SeedPassEncoder(
    override val key: String
) : PassEncoder() {

    companion object {
        internal val allSeedPassEncoders by lazy {
            listOf(
                Bip39L12PassEncoder,
                Bip39L15PassEncoder,
                Bip39L18PassEncoder,
                Bip39L21PassEncoder,
                Bip39L24PassEncoder
            )
        }
    }

    object Bip39L12PassEncoder : SeedPassEncoder(KEY_PASS_ENCODER_BIP39_12_WORDS) {
        override val instance = Bip39Codec(Strength.WORDS_12)
    }

    object Bip39L15PassEncoder : SeedPassEncoder(KEY_PASS_ENCODER_BIP39_15_WORDS) {
        override val instance = Bip39Codec(Strength.WORDS_15)
    }

    object Bip39L18PassEncoder : SeedPassEncoder(KEY_PASS_ENCODER_BIP39_18_WORDS) {
        override val instance = Bip39Codec(Strength.WORDS_18)
    }

    object Bip39L21PassEncoder : SeedPassEncoder(KEY_PASS_ENCODER_BIP39_21_WORDS) {
        override val instance = Bip39Codec(Strength.WORDS_21)
    }

    object Bip39L24PassEncoder : SeedPassEncoder(KEY_PASS_ENCODER_BIP39_24_WORDS) {
        override val instance = Bip39Codec(Strength.WORDS_24)
    }


    override suspend fun encode(input: ByteArray): String {
        val binaryBlockSize = (instance as Bip39Codec).strength.entropyBytes
        return instance.encode(input.copyOfRange(0, binaryBlockSize))
    }
}