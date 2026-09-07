package ir.ornix.passgen.codec

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Utf8TextCodecTest {


    private val codec = Utf8TextCodec()

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
    // ASCII
    // ---------------------------------------------------------------------

    @Test
    fun `encode ascii bytes produces expected string`() {
        val input = byteArrayOf(72, 101, 108, 108, 111) // "Hello"
        assertEquals("Hello", codec.encode(input))
    }

    @Test
    fun `decode ascii string produces expected bytes`() {
        val expected = byteArrayOf(72, 101, 108, 108, 111) // "Hello"
        assertContentEquals(expected, codec.decode("Hello"))
    }

    // ---------------------------------------------------------------------
    // No fixed block size — every input length is valid
    // ---------------------------------------------------------------------

    @Test
    fun `encode does not throw for any input size`() {
        // UTF-8 is not a fixed-block codec, so all sizes are valid.
        codec.encode(byteArrayOf())
        codec.encode(byteArrayOf(1))
        codec.encode(byteArrayOf(1, 2))
        codec.encode(byteArrayOf(1, 2, 3))
        codec.encode(byteArrayOf(1, 2, 3, 4))
        codec.encode(byteArrayOf(1, 2, 3, 4, 5))
    }

    // ---------------------------------------------------------------------
    // Multi-byte UTF-8 (accents, CJK, Arabic script, emoji / surrogate pairs)
    // ---------------------------------------------------------------------

    @Test
    fun `encode and decode known multi-byte string vectors`() {
        val cases = listOf(
            "café",
            "pässwörd",
            "日本語",
            "سلام",
            "🔒",
            "combining é\u0301", // base char + combining accent
        )

        for (text in cases) {
            val bytes = text.encodeToByteArray()
            assertEquals(text, codec.encode(bytes), "Encoding mismatch for \"$text\"")
            assertContentEquals(bytes, codec.decode(text), "Decoding mismatch for \"$text\"")
        }
    }

    // ---------------------------------------------------------------------
    // Encode/decode against known string vectors (whitespace-focused)
    // ---------------------------------------------------------------------

    @Test
    fun `encode and decode whitespace and plain text vectors`() {
        val cases = listOf(
            "",
            " ",
            " \n",
            "Hello",
            "Hello  World",
        )

        for (text in cases) {
            val bytes = text.encodeToByteArray()
            assertEquals(text, codec.encode(bytes), "Encoding mismatch for \"$text\"")
            assertContentEquals(bytes, codec.decode(text), "Decoding mismatch for \"$text\"")
        }
    }

    // ---------------------------------------------------------------------
    // Round trip
    // ---------------------------------------------------------------------

    @Test
    fun `round trip for a sentence with spaces and punctuation`() {
        val original = "The quick brown fox jumps over the lazy dog!"
        assertEquals(original, codec.encode(codec.decode(original)))
    }

    @Test
    fun `round trip for string with multiple spaces`() {
        val original = "   multiple   spaces   between   words   "
        assertEquals(original, codec.encode(codec.decode(original)))
    }

    @Test
    fun `round trip for mixed ascii and multi-byte characters`() {
        val original = "Hello, 世界! 🌍 café"
        assertEquals(original, codec.encode(codec.decode(original)))
    }

    @Test
    fun `round trip is identity for encode then decode as well`() {
        val original = "Round trip: café 🔒 日本語".encodeToByteArray()
        assertContentEquals(original, codec.decode(codec.encode(original)))
    }

    // ---------------------------------------------------------------------
    // Malformed byte sequences (lenient behavior, not an exception)
    // ---------------------------------------------------------------------

    @Test
    fun `encode replaces invalid UTF-8 byte sequences instead of throwing`() {
        // 0xFF is never valid in any position of a UTF-8 sequence.
        val invalid = byteArrayOf(0xFF.toByte(), 0xFE.toByte())
        assertEquals("\uFFFD\uFFFD", codec.encode(invalid))
    }

    @Test
    fun `encode replaces incomplete multi-byte sequence at end of input`() {
        // 0xC3 starts a 2-byte sequence but is not followed by a continuation byte.
        val truncated = byteArrayOf(0x48, 0x69, 0xC3.toByte()) // "Hi" + dangling lead byte
        assertEquals("Hi\uFFFD", codec.encode(truncated))
    }
}