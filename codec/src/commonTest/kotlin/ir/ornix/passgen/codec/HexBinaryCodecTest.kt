package ir.ornix.passgen.codec

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HexBinaryCodecTest {

    private val uppercaseCodec = HexBinaryCodec(uppercaseOutput = true)
    private val lowercaseCodec = HexBinaryCodec(uppercaseOutput = false)

    // ---------------------------------------------------------------------
    // Encode
    // ---------------------------------------------------------------------

    @Test
    fun `encode produces uppercase hex when configured`() {
        val input = byteArrayOf(0x01, 0x0A, 0xFF.toByte())
        assertEquals("010AFF", uppercaseCodec.encode(input))
    }

    @Test
    fun `encode produces lowercase hex when configured`() {
        val input = byteArrayOf(0x01, 0x0A, 0xFF.toByte())
        assertEquals("010aff", lowercaseCodec.encode(input))
    }

    @Test
    fun `encode empty byte array returns empty string`() {
        assertEquals("", uppercaseCodec.encode(ByteArray(0)))
        assertEquals("", lowercaseCodec.encode(ByteArray(0)))
    }

    @Test
    fun `encode does not throw for any input size`() {
        // blockSize is 1, so there is no partial-block case to worry about.
        uppercaseCodec.encode(byteArrayOf())
        uppercaseCodec.encode(byteArrayOf(1))
        uppercaseCodec.encode(byteArrayOf(1, 2))
        uppercaseCodec.encode(byteArrayOf(1, 2, 3))
        uppercaseCodec.encode(byteArrayOf(1, 2, 3, 4))

        lowercaseCodec.encode(byteArrayOf())
        lowercaseCodec.encode(byteArrayOf(1))
        lowercaseCodec.encode(byteArrayOf(1, 2))
        lowercaseCodec.encode(byteArrayOf(1, 2, 3))
        lowercaseCodec.encode(byteArrayOf(1, 2, 3, 4))
    }

    // ---------------------------------------------------------------------
    // Decode
    // ---------------------------------------------------------------------

    @Test
    fun `decode uppercase hex produces expected bytes`() {
        val expected = byteArrayOf(0x01, 0x0A, 0xFF.toByte())
        assertContentEquals(expected, uppercaseCodec.decode("010AFF"))
    }

    @Test
    fun `decode lowercase hex produces expected bytes`() {
        val expected = byteArrayOf(0x01, 0x0A, 0xFF.toByte())
        assertContentEquals(expected, uppercaseCodec.decode("010aff"))
    }

    @Test
    fun `decode mixed case hex produces expected bytes`() {
        // Decoding should be case-insensitive regardless of the codec's
        // own uppercaseOutput setting, since that flag only affects encode.
        val expected = byteArrayOf(0x01, 0x0A, 0xFF.toByte())
        assertContentEquals(expected, uppercaseCodec.decode("010Aff"))
        assertContentEquals(expected, lowercaseCodec.decode("010aFF"))
    }

    @Test
    fun `decode empty string returns empty byte array`() {
        assertContentEquals(ByteArray(0), uppercaseCodec.decode(""))
        assertContentEquals(ByteArray(0), lowercaseCodec.decode(""))
    }

    @Test
    fun `decode throws on odd length input`() {
        assertFailsWith<IllegalArgumentException> {
            uppercaseCodec.decode("ABC")
        }

        assertFailsWith<IllegalArgumentException> {
            uppercaseCodec.decode("abc")
        }
    }

    @Test
    fun `decode throws on non-hex characters`() {
        assertFailsWith<IllegalArgumentException> {
            uppercaseCodec.decode("ZZ")
        }

        assertFailsWith<IllegalArgumentException> {
            lowercaseCodec.decode("zz")
        }
    }

    @Test
    fun `decode throws on non-hex characters mixed with valid ones`() {
        assertFailsWith<IllegalArgumentException> {
            uppercaseCodec.decode("0G")
        }

        assertFailsWith<IllegalArgumentException> {
            lowercaseCodec.decode("0g")
        }
    }

    // ---------------------------------------------------------------------
    // Encode/decode against known string vectors
    // ---------------------------------------------------------------------

    @Test
    fun `encode and decode known string vectors`() {
        val cases = listOf(
            "" to "",
            " " to "20",
            " \n" to "200A",
            "Hello" to "48656C6C6F",
            "Hello  World" to "48656C6C6F2020576F726C64",
        )

        for ((text, hex) in cases) {
            val bytes = text.encodeToByteArray()
            assertEquals(
                hex,
                uppercaseCodec.encode(bytes),
                "Encoding mismatch for \"$text\""
            )
            assertEquals(
                hex.lowercase(),
                lowercaseCodec.encode(bytes),
                "Lowercase encoding mismatch for \"$text\""
            )
            assertContentEquals(
                bytes,
                uppercaseCodec.decode(hex),
                "Decoding mismatch for \"$hex\""
            )
            assertContentEquals(
                bytes,
                lowercaseCodec.decode(hex.lowercase()),
                "Decoding mismatch for \"${hex.lowercase()}\""
            )
        }
    }

    // ---------------------------------------------------------------------
    // Round trip
    // ---------------------------------------------------------------------

    @Test
    fun `round trip for every input length from 0 to 16`() {
        for (length in 0..16) {
            val input = ByteArray(length) { it.toByte() }
            assertContentEquals(input, uppercaseCodec.decode(uppercaseCodec.encode(input)))
            assertContentEquals(input, lowercaseCodec.decode(lowercaseCodec.encode(input)))
        }
    }

    @Test
    fun `round trip for random binary data of varying sizes`() {
        val random = Random(seed = 7)
        repeat(50) {
            val length = random.nextInt(0, 128)
            val input = random.nextBytes(length)
            assertContentEquals(input, uppercaseCodec.decode(uppercaseCodec.encode(input)))
            assertContentEquals(input, lowercaseCodec.decode(lowercaseCodec.encode(input)))
        }
    }
}