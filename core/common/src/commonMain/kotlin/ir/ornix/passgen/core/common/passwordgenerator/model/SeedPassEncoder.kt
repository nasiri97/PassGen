package ir.ornix.passgen.core.common.passwordgenerator.model

import ir.ornix.passgen.core.common.codec.Bip39Codec
import ir.ornix.passgen.core.common.codec.Bip39Codec.Strength

sealed class SeedPassEncoder(
    override val key: String
) : PassEncoder() {

    object Bip39L12PassEncoder : SeedPassEncoder(KEY_BIP39_L12_PASS_ENCODER) {
        override val instance = Bip39Codec(Strength.WORDS_12)
    }

    object Bip39L15PassEncoder : SeedPassEncoder(KEY_BIP39_L15_PASS_ENCODER) {
        override val instance = Bip39Codec(Strength.WORDS_15)
    }

    object Bip39L18PassEncoder : SeedPassEncoder(KEY_BIP39_L18_PASS_ENCODER) {
        override val instance = Bip39Codec(Strength.WORDS_18)
    }

    object Bip39L21PassEncoder : SeedPassEncoder(KEY_BIP39_L21_PASS_ENCODER) {
        override val instance = Bip39Codec(Strength.WORDS_21)
    }

    object Bip39L24PassEncoder : SeedPassEncoder(KEY_BIP39_L24_PASS_ENCODER) {
        override val instance = Bip39Codec(Strength.WORDS_24)
    }


    override fun encode(input: ByteArray): String {
        val binaryBlockSize = (instance as Bip39Codec).strength.entropyBytes
        return instance.encode(input.copyOfRange(0, binaryBlockSize))
    }
}