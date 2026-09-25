package ir.ornix.passgen.core.common.codec

import ir.ornix.passgen.core.common.codec.core.Codec
import ir.ornix.passgen.core.common.codec.util.BIP39_ENGLISH_WORDS
import ir.ornix.passgen.core.common.hashing.Sha256Hasher

/**
 * BIP-39 mnemonic codec.
 *
 * Converts cryptographic entropy to a BIP-39 mnemonic sentence and
 * converts a BIP-39 mnemonic sentence back to its original entropy.
 *
 * This class implements only the mnemonic encoding/decoding part of BIP-39.
 * It does not perform BIP-39 seed derivation (PBKDF2-HMAC-SHA512).
 *
 * The [strength] determines the required entropy size and the resulting
 * mnemonic word count.
 *
 * For example:
 *
 * - [Strength.WORDS_12] → 128-bit entropy → 12 words
 * - [Strength.WORDS_15] → 160-bit entropy → 15 words
 * - [Strength.WORDS_18] → 192-bit entropy → 18 words
 * - [Strength.WORDS_21] → 224-bit entropy → 21 words
 * - [Strength.WORDS_24] → 256-bit entropy → 24 words
 *
 * BIP-39 encoding:
 *
 *     Entropy
 *        ↓
 *     SHA-256(entropy)
 *        ↓
 *     Checksum = ENT / 32 bits
 *        ↓
 *     Entropy + Checksum
 *        ↓
 *     Split into 11-bit groups
 *        ↓
 *     BIP-39 word list
 *        ↓
 *     Mnemonic
 *
 * BIP-39 decoding performs the reverse operation and validates the
 * checksum before returning the entropy.
 */
class Bip39Codec(val strength: Strength = Strength.WORDS_24) : Codec {

    /**
     * BIP-39 entropy strength.
     *
     * The entropy size determines the checksum size and mnemonic length.
     *
     * The BIP-39 specification supports exactly five entropy sizes:
     * 128, 160, 192, 224, and 256 bits.
     */
    enum class Strength(
        /**
         * Required entropy size in bytes.
         */
        val entropyBytes: Int,

        /**
         * Number of words in the resulting mnemonic.
         */
        val wordCount: Int,
    ) {
        /** 128-bit entropy + 4-bit checksum → 12-word mnemonic. */
        WORDS_12(
            entropyBytes = 16,
            wordCount = 12,
        ),

        /** 160-bit entropy + 5-bit checksum → 15-word mnemonic. */
        WORDS_15(
            entropyBytes = 20,
            wordCount = 15,
        ),

        /** 192-bit entropy + 6-bit checksum → 18-word mnemonic. */
        WORDS_18(
            entropyBytes = 24,
            wordCount = 18,
        ),

        /** 224-bit entropy + 7-bit checksum → 21-word mnemonic. */
        WORDS_21(
            entropyBytes = 28,
            wordCount = 21,
        ),

        /** 256-bit entropy + 8-bit checksum → 24-word mnemonic. */
        WORDS_24(
            entropyBytes = 32,
            wordCount = 24,
        )
    }

    private companion object {
        /**
         * Each BIP-39 word represents an 11-bit index into the
         * 2048-word English word list.
         */
        const val WORD_BITS = 11
    }

    private val sha256 = Sha256Hasher()

    /**
     * Official BIP-39 English word list.
     *
     * The list contains exactly 2048 words, indexed from 0 to 2047.
     */
    private val wordList = BIP39_ENGLISH_WORDS

    /**
     * Reverse lookup table used when decoding a mnemonic.
     *
     * Instead of searching the 2048-word array for every word,
     * this map directly resolves a word to its BIP-39 index.
     */
    private val wordToIndex: Map<String, Int> by lazy {
        wordList.withIndex().associate { (index, word) ->
            word to index
        }
    }

    /**
     * Encodes entropy into a BIP-39 mnemonic sentence.
     *
     * The input entropy must have the exact size specified by [strength].
     *
     * The checksum consists of the first `ENT / 32` bits of
     * SHA-256(entropy), where `ENT` is the entropy size in bits.
     *
     * The entropy and checksum are concatenated and split into
     * 11-bit groups. Each group is interpreted as an index into
     * the BIP-39 English word list.
     *
     * @param input cryptographically secure random entropy.
     * @return BIP-39 mnemonic sentence.
     * @throws IllegalArgumentException if the entropy size does not
     * match the configured [strength].
     */
    override fun encode(input: ByteArray): String {
        require(input.size == strength.entropyBytes) {
            "BIP-39 ${strength.wordCount}-word mnemonic requires " +
                    "${strength.entropyBytes} bytes of entropy, " +
                    "got ${input.size} bytes."
        }

        val entropyBits = input.size * 8

        // BIP-39 defines the checksum length as ENT / 32.
        val checksumBits = entropyBits / 32

        // SHA-256 provides the checksum bits.
        val checksum = sha256.digestBlocking(input)[0]
            .toInt()
            .and(0xFF)
            .toString(2)
            .padStart(8, '0')

        /*
         * Concatenate:
         *
         *     ENTROPY || CHECKSUM
         *
         * The resulting bit length is always divisible by 11 for
         * the entropy sizes supported by BIP-39.
         */
        val bits = buildString(entropyBits + checksumBits) {
            input.forEach { append(it.toBinaryString()) }
            append(checksum.take(checksumBits))
        }

        /*
         * Every 11-bit group is an integer in the range 0..2047,
         * which directly indexes the BIP-39 word list.
         */
        return bits.chunked(WORD_BITS)
            .joinToString(" ") { chunk ->
                wordList[chunk.toInt(2)]
            }
    }

    /**
     * Decodes a BIP-39 mnemonic sentence back into its original entropy.
     *
     * The number of words must match the [strength] configured for this
     * codec instance. The checksum is recalculated and verified before
     * the entropy is returned.
     *
     * @param input BIP-39 mnemonic sentence.
     * @return the original entropy represented by the mnemonic.
     * @throws IllegalArgumentException if the word count is incorrect,
     * a word is not present in the word list, or the checksum is invalid.
     */
    override fun decode(input: String): ByteArray {
        val words = input.trim().split(Regex("\\s+"))

        require(words.size == strength.wordCount) {
            "Expected a ${strength.wordCount}-word BIP-39 mnemonic, " +
                    "got ${words.size} words."
        }

        /*
         * Convert every word back into its 11-bit word-list index.
         */
        val bits = buildString(words.size * WORD_BITS) {
            words.forEach { word ->
                val index = wordToIndex[word]
                    ?: throw IllegalArgumentException(
                        "Unknown BIP-39 word: '$word'"
                    )

                append(
                    index
                        .toString(2)
                        .padStart(WORD_BITS, '0')
                )
            }
        }

        val entropyBits = strength.entropyBytes * 8

        // The checksum length is always ENT / 32.
        val checksumBits = entropyBits / 32

        /*
         * The first ENT bits represent the original entropy.
         * The remaining CS bits represent the checksum.
         */
        val entropy = ByteArray(strength.entropyBytes)

        for (i in entropy.indices) {
            val start = i * 8

            entropy[i] = bits
                .substring(start, start + 8)
                .toInt(2)
                .toByte()
        }

        /*
         * Extract the checksum encoded in the mnemonic.
         */
        val expectedChecksum = bits.takeLast(checksumBits)

        /*
         * Recalculate the checksum from the recovered entropy.
         */
        val actualChecksum = sha256
            .digestBlocking(entropy)[0]
            .toInt()
            .and(0xFF)
            .toString(2)
            .padStart(8, '0')
            .take(checksumBits)

        require(actualChecksum == expectedChecksum) {
            "Invalid BIP-39 mnemonic checksum."
        }

        return entropy
    }

    /**
     * Converts a byte to its unsigned 8-bit binary representation.
     *
     * For example:
     *
     *     0x05 → "00000101"
     */
    private fun Byte.toBinaryString(): String =
        (toInt() and 0xFF)
            .toString(2)
            .padStart(8, '0')
}