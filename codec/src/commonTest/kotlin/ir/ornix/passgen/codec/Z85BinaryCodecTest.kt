package ir.ornix.passgen.codec

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Z85BinaryCodecTest {

    private val codec = Z85BinaryCodec()

    // ---------------------------------------------------------------------
    // Empty input
    // ---------------------------------------------------------------------

    @Test
    fun `encode empty byte array returns empty string`() {
        assertEquals("", codec.encode(ByteArray(0)))
    }

    @Test
    fun `decode empty string returns empty byte array`() {
        assertContentEquals(ByteArray(0), codec.decode(""))
    }

    // ---------------------------------------------------------------------
    // Known, official Z85 test vector (from the ZeroMQ RFC 32 spec)
    // ---------------------------------------------------------------------

    @Test
    fun `encode official spec vector produces HelloWorld`() {
        val input = byteArrayOf(
            0x86.toByte(), 0x4F, 0xD2.toByte(), 0x6F,
            0xB5.toByte(), 0x59, 0xF7.toByte(), 0x5B
        )
        assertEquals("HelloWorld", codec.encode(input))
    }

    @Test
    fun `decode HelloWorld produces official spec vector bytes`() {
        val expected = byteArrayOf(
            0x86.toByte(), 0x4F, 0xD2.toByte(), 0x6F,
            0xB5.toByte(), 0x59, 0xF7.toByte(), 0x5B
        )
        assertContentEquals(expected, codec.decode("HelloWorld"))
    }

    @Test
    fun `encode and decode known string vectors`() {
        val cases = listOf(
            "" to "",
            "    " to "arR^H",
            "    \n\n\n\n" to "arR^H3jmaE",
            "Hell" to "nm=QN",
            "Hello  World" to "nm=QNzY*f7z/PV8",
        )

        for ((text, z85) in cases) {
            val bytes = text.encodeToByteArray()
            assertEquals(z85, codec.encode(bytes), "Encoding mismatch for \"$text\"")
            assertContentEquals(bytes, codec.decode(z85), "Decoding mismatch for \"$z85\"")
        }
    }

    @Test
    fun `encode and decode simple byte vectors`() {
        val cases = listOf(
            byteArrayOf(0, 0, 0, 0) to "00000",
            byteArrayOf(0, 0, 0, 1) to "00001",
            byteArrayOf(0, 0, 1, 0) to "00031",
            byteArrayOf(0, 1, 0, 0) to "00961",
            byteArrayOf(1, 0, 0, 0) to "0rr91",
        )

        for ((bytes, expected) in cases) {
            assertEquals(
                expected,
                codec.encode(bytes),
                "Failed encoding ${bytes.contentToString()}"
            )
            assertContentEquals(
                bytes,
                codec.decode(expected),
                "Failed decoding \"$expected\""
            )
        }
    }

    // ---------------------------------------------------------------------
    // Output size: each 4-byte block becomes exactly 5 characters
    // ---------------------------------------------------------------------

    @Test
    fun `encoded size follows 4 bytes to 5 chars ratio`() {
        for (blocks in 0..5) {
            val input = ByteArray(blocks * 4)
            assertEquals(blocks * 5, codec.encode(input).length)
        }
    }

    @Test
    fun `decoded size follows 5 chars to 4 bytes ratio`() {
        val random = Random(seed = 11)
        for (blocks in 0..5) {
            val input = random.nextBytes(blocks * 4)
            val encoded = codec.encode(input)
            assertEquals(blocks * 4, codec.decode(encoded).size)
        }
    }

    // ---------------------------------------------------------------------
    // Round trip (only defined for multiples of 4 bytes — see spec note)
    // ---------------------------------------------------------------------

    @Test
    fun `round trip for block-aligned lengths from 0 to 100`() {
        val random = Random(42)

        for (length in 0..100 step 4) {
            val input = ByteArray(length) { random.nextInt().toByte() }
            val encoded = codec.encode(input)
            val decoded = codec.decode(encoded)
            assertContentEquals(input, decoded, "Round trip failed for length $length")
        }
    }

    @Test
    fun `round trip for random block-aligned binary data`() {
        val random = Random(seed = 5)
        repeat(50) {
            val length = random.nextInt(0, 20) * 4
            val input = random.nextBytes(length)
            val encoded = codec.encode(input)
            val decoded = codec.decode(encoded)
            assertContentEquals(input, decoded, "Round trip failed for length $length")
        }
    }

    // ---------------------------------------------------------------------
    // Alphabet conformance
    // ---------------------------------------------------------------------

    @Test
    fun `encode uses only characters from the Z85 alphabet`() {
        val alphabet =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-:+=^!/*?&<>()[]{}@%$#".toSet()
        val random = Random(seed = 17)
        repeat(50) {
            val encoded = codec.encode(random.nextBytes(4 * random.nextInt(1, 10)))
            assertEquals(emptySet(), encoded.toSet() - alphabet)
        }
    }

    // ---------------------------------------------------------------------
    // Error handling
    // ---------------------------------------------------------------------

    @Test
    fun `encode throws when input length is not a multiple of 4`() {
        assertFailsWith<IllegalArgumentException> {
            codec.encode(byteArrayOf(1))
        }

        assertFailsWith<IllegalArgumentException> {
            codec.encode(byteArrayOf(1, 2))
        }

        assertFailsWith<IllegalArgumentException> {
            codec.encode(byteArrayOf(1, 2, 3))
        }
    }

    @Test
    fun `decode throws when input length is not a multiple of 5`() {
        assertFailsWith<IllegalArgumentException> {
            codec.decode("a")
        }

        assertFailsWith<IllegalArgumentException> {
            codec.decode("ab")
        }

        assertFailsWith<IllegalArgumentException> {
            codec.decode("abc")
        }

        assertFailsWith<IllegalArgumentException> {
            codec.decode("abcd")
        }
    }

    @Test
    fun `decode throws on character outside the Z85 alphabet`() {
        assertFailsWith<IllegalArgumentException> {
            codec.decode("abc\"d")
        }

        assertFailsWith<IllegalArgumentException> {
            codec.decode("abc i") // space is not in the Z85 alphabet
        }
    }
}
