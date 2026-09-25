package ir.ornix.passgen.core.common.codec

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Bip39CodecTest {

    private data class Vector(
        val strength: Bip39Codec.Strength,
        val entropyHex: String,
        val mnemonic: String,
    )

    private val vectors = listOf(
        // WORDS_12 - 128-bit entropy / 16 bytes (official vectors)
        Vector(
            Bip39Codec.Strength.WORDS_12,
            "00".repeat(16),
            "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about",
        ),
        Vector(
            Bip39Codec.Strength.WORDS_12,
            "7f".repeat(16),
            "legal winner thank year wave sausage worth useful legal winner thank yellow",
        ),
        Vector(
            Bip39Codec.Strength.WORDS_12,
            "80".repeat(16),
            "letter advice cage absurd amount doctor acoustic avoid letter advice cage above",
        ),
        Vector(
            Bip39Codec.Strength.WORDS_12,
            "ff".repeat(16),
            "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo wrong",
        ),

        // WORDS_15 - 160-bit entropy / 20 bytes (derived, see note above)
        Vector(
            Bip39Codec.Strength.WORDS_15,
            "00".repeat(20),
            "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon " +
                    "abandon abandon abandon abandon address",
        ),

        // WORDS_18 - 192-bit entropy / 24 bytes (official vectors)
        Vector(
            Bip39Codec.Strength.WORDS_18,
            "00".repeat(24),
            "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon " +
                    "abandon abandon abandon abandon abandon abandon abandon agent",
        ),
        Vector(
            Bip39Codec.Strength.WORDS_18,
            "7f".repeat(24),
            "legal winner thank year wave sausage worth useful legal winner thank year " +
                    "wave sausage worth useful legal will",
        ),
        Vector(
            Bip39Codec.Strength.WORDS_18,
            "80".repeat(24),
            "letter advice cage absurd amount doctor acoustic avoid letter advice cage absurd " +
                    "amount doctor acoustic avoid letter always",
        ),
        Vector(
            Bip39Codec.Strength.WORDS_18,
            "ff".repeat(24),
            "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo when",
        ),

        // WORDS_21 - 224-bit entropy / 28 bytes (derived, see note above)
        Vector(
            Bip39Codec.Strength.WORDS_21,
            "00".repeat(28),
            "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon " +
                    "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon admit",
        ),

        // WORDS_24 - 256-bit entropy / 32 bytes (official vectors)
        Vector(
            Bip39Codec.Strength.WORDS_24,
            "00".repeat(32),
            "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon " +
                    "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon " +
                    "abandon abandon abandon art",
        ),
        Vector(
            Bip39Codec.Strength.WORDS_24,
            "7f".repeat(32),
            "legal winner thank year wave sausage worth useful legal winner thank year " +
                    "wave sausage worth useful legal winner thank year wave sausage worth title",
        ),
        Vector(
            Bip39Codec.Strength.WORDS_24,
            "80".repeat(32),
            "letter advice cage absurd amount doctor acoustic avoid letter advice cage absurd " +
                    "amount doctor acoustic avoid letter advice cage absurd amount doctor acoustic bless",
        ),
        Vector(
            Bip39Codec.Strength.WORDS_24,
            "ff".repeat(32),
            "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo " +
                    "zoo zoo zoo vote",
        ),
    )


    @Test
    fun encode_returnsExpectedMnemonic_forEachKnownVectorAndStrength() {
        vectors.forEach { vector ->
            val codec = Bip39Codec(vector.strength)
            val entropy = vector.entropyHex.hexToByteArray()

            val actual = codec.encode(entropy)

            assertEquals(
                vector.mnemonic,
                actual,
                "encode() mismatch for ${vector.strength} with entropy ${vector.entropyHex}",
            )
        }
    }

    @Test
    fun encode_producesTheExpectedWordCount_forEachStrength() {
        Bip39Codec.Strength.entries.forEach { strength ->
            val codec = Bip39Codec(strength)
            val entropy = ByteArray(strength.entropyBytes) { 0x42 }

            val words = codec.encode(entropy).split(" ")

            assertEquals(strength.wordCount, words.size, "word count mismatch for $strength")
        }
    }

    @Test
    fun encode_throws_whenEntropySizeDoesNotMatchStrength() {
        val codec = Bip39Codec(Bip39Codec.Strength.WORDS_12)

        assertFailsWith<IllegalArgumentException> {
            codec.encode(ByteArray(15)) // WORDS_12 requires 16 bytes
        }
    }


    @Test
    fun decode_recoversOriginalEntropy_forEachKnownVectorAndStrength() {
        vectors.forEach { vector ->
            val codec = Bip39Codec(vector.strength)
            val expectedEntropy = vector.entropyHex.hexToByteArray()

            val actual = codec.decode(vector.mnemonic)

            assertContentEquals(
                expectedEntropy,
                actual,
                "decode() mismatch for ${vector.strength} with mnemonic '${vector.mnemonic}'",
            )
        }
    }

    @Test
    fun decode_ignoresExtraWhitespace() {
        val codec = Bip39Codec(Bip39Codec.Strength.WORDS_12)
        val messy = "  abandon  abandon abandon abandon abandon abandon abandon abandon " +
                "abandon abandon abandon about  "

        val entropy = codec.decode(messy)

        assertContentEquals(ByteArray(16), entropy)
    }

    @Test
    fun decode_throws_whenWordCountDoesNotMatchStrength() {
        val codec = Bip39Codec(Bip39Codec.Strength.WORDS_12)

        assertFailsWith<IllegalArgumentException> {
            codec.decode("abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon")
        }
    }

    @Test
    fun decode_throws_whenMnemonicContainsAnUnknownWord() {
        val codec = Bip39Codec(Bip39Codec.Strength.WORDS_12)

        assertFailsWith<IllegalArgumentException> {
            codec.decode(
                "abandon abandon abandon abandon abandon abandon abandon abandon " +
                        "abandon abandon abandon notarealbip39word",
            )
        }
    }

    @Test
    fun decode_throws_whenChecksumIsInvalid() {
        val codec = Bip39Codec(Bip39Codec.Strength.WORDS_12)
        // Valid words, but "ability" as the last word breaks the checksum
        // that was computed for the all-zero entropy ("...about" is correct).
        val badChecksum = "abandon abandon abandon abandon abandon abandon abandon abandon " +
                "abandon abandon abandon ability"

        assertFailsWith<IllegalArgumentException> {
            codec.decode(badChecksum)
        }
    }


    @Test
    fun encodeThenDecode_returnsOriginalEntropy_forEachStrength() {
        Bip39Codec.Strength.entries.forEach { strength ->
            val codec = Bip39Codec(strength)
            val originalEntropy = ByteArray(strength.entropyBytes) { i -> (i * 31 + 7).toByte() }

            val mnemonic = codec.encode(originalEntropy)
            val recoveredEntropy = codec.decode(mnemonic)

            assertContentEquals(
                originalEntropy,
                recoveredEntropy,
                "round trip failed for $strength",
            )
            assertEquals(strength.wordCount, mnemonic.split(" ").size)
        }
    }

    @Test
    fun defaultStrength_isWords24() {
        val codec = Bip39Codec()
        assertEquals(codec.strength, Bip39Codec.Strength.WORDS_24)
    }

    private fun String.hexToByteArray(): ByteArray {
        check(length % 2 == 0) { "Hex string must have an even length: $this" }
        return ByteArray(length / 2) { i ->
            substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    }
}