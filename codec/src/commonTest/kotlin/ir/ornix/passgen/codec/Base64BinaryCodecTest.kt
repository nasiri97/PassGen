package ir.ornix.passgen.codec

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Base64BinaryCodecTest {

    private val codec = Base64BinaryCodec()

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
    // Known RFC 4648 test vectors ("f", "fo", "foo", "foob", "fooba", "foobar")
    // ---------------------------------------------------------------------

    @Test
    fun `encode single block produces expected string`() {
        val input = byteArrayOf(77, 97, 110) // "Man"
        val expected = "TWFu"
        assertEquals(expected, codec.encode(input))
    }

    @Test
    fun `encode multiple blocks produces expected string`() {
        val input = "ManMan".encodeToByteArray()
        val expected = "TWFuTWFu"
        assertEquals(expected, codec.encode(input))
    }

    @Test
    fun `encode f produces Zg with padding`() {
        assertEquals("Zg==", codec.encode("f".encodeToByteArray()))
    }

    @Test
    fun `encode fo produces Zm8 with padding`() {
        assertEquals("Zm8=", codec.encode("fo".encodeToByteArray()))
    }

    @Test
    fun `encode foo produces Zm9v with no padding`() {
        assertEquals("Zm9v", codec.encode("foo".encodeToByteArray()))
    }

    @Test
    fun `encode foob produces Zm9vYg with padding`() {
        assertEquals("Zm9vYg==", codec.encode("foob".encodeToByteArray()))
    }

    @Test
    fun `encode fooba produces Zm9vYmE with padding`() {
        assertEquals("Zm9vYmE=", codec.encode("fooba".encodeToByteArray()))
    }

    @Test
    fun `encode foobar produces Zm9vYmFy with no padding`() {
        assertEquals("Zm9vYmFy", codec.encode("foobar".encodeToByteArray()))
    }

    @Test
    fun `decode single block produces expected bytes`() {
        val input = "TWFu"
        val expected = byteArrayOf(77, 97, 110) // "Man"
        val actual = codec.decode(input)
        assertContentEquals(expected, actual)
    }

    @Test
    fun `decode multiple blocks produces expected bytes`() {
        val input = "TWFuTWFu"
        val expected = "ManMan".encodeToByteArray()
        assertContentEquals(expected, codec.decode(input))
    }


    @Test
    fun `decode Zg produces f`() {
        assertEquals("f", codec.decode("Zg==").decodeToString())
        assertEquals("f", codec.decode("Zg").decodeToString())
    }

    @Test
    fun `decode Zm9v produces foo`() {
        assertEquals("foo", codec.decode("Zm9v").decodeToString())
    }

    @Test
    fun `decode Zm9vYmFy produces foobar`() {
        assertEquals("foobar", codec.decode("Zm9vYmFy").decodeToString())
    }

    // ---------------------------------------------------------------------
    // Padding shape
    // ---------------------------------------------------------------------

    @Test
    fun `encoded length is always a multiple of 4`() {
        for (length in 0..12) {
            val encoded = codec.encode(ByteArray(length))
            assertEquals(0, encoded.length % 4, "Length $length produced non-multiple-of-4 output")
        }
    }

    @Test
    fun `one trailing byte produces two padding characters`() {
        val encoded = codec.encode(ByteArray(1))
        assertEquals("==", encoded.takeLast(2))
    }

    @Test
    fun `two trailing bytes produce one padding character`() {
        val encoded = codec.encode(ByteArray(2))
        assertEquals('=', encoded.last())
        assertEquals(false, encoded.dropLast(1).endsWith("="))
    }

    @Test
    fun `three bytes produce no padding`() {
        val encoded = codec.encode(ByteArray(3))
        assertEquals(false, encoded.contains('='))
    }

    // ---------------------------------------------------------------------
    // Round trip
    // ---------------------------------------------------------------------

    @Test
    fun `round trip for every input length from 0 to 16`() {
        for (length in 0..16) {
            val input = ByteArray(length) { it.toByte() }
            val decoded = codec.decode(codec.encode(input))
            assertContentEquals(input, decoded, "Round trip failed for length $length")
        }
    }

    @Test
    fun `round trip for random binary data of varying sizes`() {
        val random = Random(seed = 7)
        repeat(50) {
            val length = random.nextInt(0, 128)
            val input = random.nextBytes(length)
            val decoded = codec.decode(codec.encode(input))
            assertContentEquals(input, decoded, "Round trip failed for length $length")
        }
    }

    @Test
    fun `round trip for string with multi-byte UTF-8 characters`() {
        val original = "pässwörd-日本語-🔒"
        val decoded = codec.decode(codec.encode(original.encodeToByteArray())).decodeToString()
        assertEquals(original, decoded)
    }

    @Test
    fun `round trip for string with multiple spaces`() {
        val original = "   multiple   spaces   between   words   "
        val decoded = codec.decode(codec.encode(original.encodeToByteArray())).decodeToString()
        assertEquals(original, decoded)
    }

    @Test
    fun `round trip for a full sentence`() {
        val original = "The quick brown fox jumps over the lazy dog, again and again!"
        val decoded = codec.decode(codec.encode(original.encodeToByteArray())).decodeToString()
        assertEquals(original, decoded)
    }

    // ---------------------------------------------------------------------
    // Alphabet conformance
    // ---------------------------------------------------------------------

    @Test
    fun `encode uses only characters from the standard base64 alphabet`() {
        val alphabet = ('A'..'Z') + ('a'..'z') + ('0'..'9') + '+' + '/' + '='
        val random = Random(seed = 99)
        repeat(50) {
            val encoded = codec.encode(random.nextBytes(32))
            assertEquals(emptySet(), encoded.toSet() - alphabet.toSet())
        }
    }

    // ---------------------------------------------------------------------
    // Test optional Padding
    // ---------------------------------------------------------------------

    @Test
    fun `decode succeeds with or without optional padding`() {
        // 2-char group (1 byte payload) — last char must be multiple of 16
        assertContentEquals(codec.decode("AA"), codec.decode("AA=="))
        assertContentEquals(codec.decode("AQ"), codec.decode("AQ=="))
        assertContentEquals(codec.decode("gA"), codec.decode("gA=="))

        // 3-char group (2 byte payload) — last char must be multiple of 4
        assertContentEquals(codec.decode("ABA"), codec.decode("ABA="))
        assertContentEquals(codec.decode("ABQ"), codec.decode("ABQ="))


        // multi-block input: full blocks followed by a padded remainder
        assertContentEquals(codec.decode("ABCDAA"), codec.decode("ABCDAA=="))
        assertContentEquals(codec.decode("ABCDABA"), codec.decode("ABCDABA="))
    }

    // ---------------------------------------------------------------------
    // Error handling
    // ---------------------------------------------------------------------

    @Test
    fun `decode throws on invalid character`() {
        assertFailsWith<IllegalArgumentException> {
            codec.decode("!!!!")
        }
    }

    @Test
    fun `decode throws when input length with padding is not a multiple of 4`() {
        assertFailsWith<IllegalArgumentException> {
            codec.decode("Zg=")
        }
    }

    @Test
    fun `decode throws when padding bits are non-zero`() {
        assertFailsWith<IllegalArgumentException> {
            codec.decode("Zm7=")
        }
    }

    // ---------------------------------------------------------------------
// Encode size: output length as a function of input length
// ---------------------------------------------------------------------

    @Test
    fun `encoded size follows base64 formula for 0 to 12 bytes`() {
        // encoded length = ceil(n / 3) * 4
        for (length in 0..12) {
            val input = ByteArray(length)
            val expectedSize = ((length + 2) / 3) * 4
            assertEquals(
                expectedSize,
                codec.encode(input).length,
                "Unexpected encoded size for input length $length"
            )
        }
    }

    @Test
    fun `encoded size for 3-byte multiples has no padding overhead`() {
        // For inputs that are exact multiples of 3, encoded size is exactly n/3*4
        for (multiple in 0..5) {
            val length = multiple * 3
            val input = ByteArray(length)
            assertEquals(multiple * 4, codec.encode(input).length)
        }
    }

    @Test
    fun `encoded size grows by 4 characters for every 3 additional bytes`() {
        val random = Random(seed = 3)
        var previousSize = codec.encode(random.nextBytes(0)).length
        for (length in 3..30 step 3) {
            val currentSize = codec.encode(random.nextBytes(length)).length
            assertEquals(4, currentSize - previousSize, "Growth mismatch at length $length")
            previousSize = currentSize
        }
    }


// ---------------------------------------------------------------------
// Decode size: output length as a function of encoded input length
// ---------------------------------------------------------------------

    @Test
    fun `decoded size matches original input size for 0 to 12 bytes`() {
        for (length in 0..12) {
            val input = ByteArray(length) { it.toByte() }
            val encoded = codec.encode(input)
            assertEquals(
                length,
                codec.decode(encoded).size,
                "Decoded size mismatch for original length $length"
            )
        }
    }

    @Test
    fun `decoded size for random data of varying lengths matches input size`() {
        val random = Random(seed = 21)
        repeat(30) {
            val length = random.nextInt(0, 100)
            val input = random.nextBytes(length)
            val encoded = codec.encode(input)
            assertEquals(length, codec.decode(encoded).size)
        }
    }

    @Test
    fun `decoded size accounts for padding characters correctly`() {
        // 1 padding char '=' -> 2 real bytes encoded; 2 padding chars '==' -> 1 real byte
        assertEquals(1, codec.decode("Zg==").size)   // "f"
        assertEquals(2, codec.decode("Zm8=").size)   // "fo"
        assertEquals(3, codec.decode("Zm9v").size)   // "foo" (no padding)
    }
}
