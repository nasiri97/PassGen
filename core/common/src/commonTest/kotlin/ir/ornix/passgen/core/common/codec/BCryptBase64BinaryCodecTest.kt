package ir.ornix.passgen.core.common.codec


import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BCryptBase64BinaryCodecTest {

    private val codec = BCryptBase64BinaryCodec()

    // ---------------------------------------------------------------------
    // Empty input
    // ---------------------------------------------------------------------
    @Test
    fun `encode empty byte array returns empty string`() = runTest {
        assertEquals("", codec.encode(ByteArray(0)))
    }

    @Test
    fun `decode empty string returns empty byte array`() = runTest {
        assertContentEquals(ByteArray(0), codec.decode(""))
    }

    // ---------------------------------------------------------------------
    // Known, hand-verified vectors
    // ---------------------------------------------------------------------

    @Test
    fun `encode single zero byte produces two dot characters`() = runTest {
        assertEquals("..", codec.encode(byteArrayOf(0x00)))
    }

    @Test
    fun `decode two dot characters produces single zero byte`() = runTest {
        assertContentEquals(byteArrayOf(0x00), codec.decode(".."))
    }

    @Test
    fun `encode single 0xFF byte matches expected characters`() = runTest {
        // 0xFF -> 111111 11(0000) -> alphabet[63]='9', alphabet[48]='u'
        assertEquals("9u", codec.encode(byteArrayOf(0xFF.toByte())))
    }

    @Test
    fun `decode 9u produces single 0xFF byte`() = runTest {
        assertContentEquals(byteArrayOf(0xFF.toByte()), codec.decode("9u"))
    }

    @Test
    fun `encode three zero bytes produces four dot characters`() = runTest {
        // A full 3-byte block of zeros maps to four 6-bit zero groups.
        assertEquals("....", codec.encode(byteArrayOf(0x00, 0x00, 0x00)))
    }

    @Test
    fun `encode three 0xFF bytes produces four 9 characters`() = runTest {
        // A full 3-byte block of 0xFF maps to four 6-bit all-ones groups (index 63).
        val input = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte())
        assertEquals("9999", codec.encode(input))
    }

    // ---------------------------------------------------------------------
    // Output length shape (no padding characters, unlike RFC 4648 Base64)
    // ---------------------------------------------------------------------

    @Test
    fun `encoded length has no padding for 1-byte input`() = runTest {
        assertEquals(2, codec.encode(ByteArray(1)).length)
    }

    @Test
    fun `encoded length has no padding for 2-byte input`() = runTest {
        assertEquals(3, codec.encode(ByteArray(2)).length)
    }

    @Test
    fun `encoded length is exact multiple of 4 for 3-byte input`() = runTest {
        assertEquals(4, codec.encode(ByteArray(3)).length)
    }

    // ---------------------------------------------------------------------
    // Round-trip correctness across block-boundary sizes
    // ---------------------------------------------------------------------

    @Test
    fun `round trip for every input length from 0 to 16`() = runTest {
        for (length in 0..16) {
            val input = ByteArray(length) { it.toByte() }
            val encoded = codec.encode(input)
            val decoded = codec.decode(encoded)
            assertContentEquals(input, decoded, "Round trip failed for length $length")
        }
    }

    @Test
    fun `round trip for random data of typical bcrypt salt size`() = runTest {
        val random = Random(seed = 42)
        repeat(50) {
            val input = random.nextBytes(16)
            val encoded = codec.encode(input)
            val decoded = codec.decode(encoded)
            assertContentEquals(input, decoded)
        }
    }

    @Test
    fun `round trip for random data of varying sizes`() = runTest {
        val random = Random(seed = 7)
        repeat(50) {
            val length = random.nextInt(0, 64)
            val input = random.nextBytes(length)
            val encoded = codec.encode(input)
            val decoded = codec.decode(encoded)
            assertContentEquals(input, decoded, "Round trip failed for length $length")
        }
    }

    @Test
    fun `encode uses only characters from the bcrypt alphabet`() = runTest {
        val alphabet = "./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toSet()
        val random = Random(seed = 99)
        repeat(50) {
            val input = random.nextBytes(32)
            val encoded = codec.encode(input)
            assertEquals(emptySet(), encoded.toSet() - alphabet)
        }
    }

    // ---------------------------------------------------------------------
    // Error handling
    // ---------------------------------------------------------------------

    @Test
    fun `decode throws on character outside the alphabet`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            codec.decode("+++")
        }
    }

    @Test
    fun `decode throws on character with code point beyond decoding table size`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            codec.decode("é")
        }
    }

    @Test
    fun `decode throws on standard base64 padding character`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            codec.decode("ab==")
        }
    }

    // ---------------------------------------------------------------------
    // Known, hand-verified vector
    // ---------------------------------------------------------------------

    @Test
    fun `encode abc produces expected string`() = runTest {
        // 'a'=0x61, 'b'=0x62, 'c'=0x63 -> a full 3-byte block
        assertEquals("WUHh", codec.encode("abc".encodeToByteArray()))
    }

    @Test
    fun `decode WUHh produces abc bytes`() = runTest {
        assertContentEquals(
            "abc".encodeToByteArray(),
            codec.decode("WUHh")
        )
    }

    // ---------------------------------------------------------------------
    // Round trip for various strings
    // ---------------------------------------------------------------------

    @Test
    fun `round trip for empty string`() = runTest {
        val original = ""
        val encoded = codec.encode(original.encodeToByteArray())
        val decoded = codec.decode(encoded).decodeToString()
        assertEquals(original, decoded)
    }

    @Test
    fun `round trip for single character`() = runTest {
        val original = "a"
        val encoded = codec.encode(original.encodeToByteArray())
        val decoded = codec.decode(encoded).decodeToString()
        assertEquals(original, decoded)
    }

    @Test
    fun `round trip for short word`() = runTest {
        val original = "hello"
        val encoded = codec.encode(original.encodeToByteArray())
        val decoded = codec.decode(encoded).decodeToString()
        assertEquals(original, decoded)
    }

    @Test
    fun `round trip for sentence with spaces and punctuation`() = runTest {
        val original = "The quick brown fox jumps over the lazy dog!"
        val encoded = codec.encode(original.encodeToByteArray())
        val decoded = codec.decode(encoded).decodeToString()
        assertEquals(original, decoded)
    }

    @Test
    fun `round trip for string containing only whitespace`() = runTest {
        val original = "   "
        val encoded = codec.encode(original.encodeToByteArray())
        val decoded = codec.decode(encoded).decodeToString()
        assertEquals(original, decoded)
    }

    @Test
    fun `round trip for typical bcrypt-style password`() = runTest {
        val original = "P@ssw0rd!2024#Secure"
        val encoded = codec.encode(original.encodeToByteArray())
        val decoded = codec.decode(encoded).decodeToString()
        assertEquals(original, decoded)
    }

    @Test
    fun `round trip for multi-byte UTF-8 characters`() = runTest {
        val original = "pässwörd-日本語-🔒"
        val encoded = codec.encode(original.encodeToByteArray())
        val decoded = codec.decode(encoded).decodeToString()
        assertEquals(original, decoded)
    }

    @Test
    fun `round trip for string with length spanning multiple block boundaries`() = runTest {
        // 31 chars: not a multiple of 3, exercises the partial-block tail logic
        val original = "abcdefghijklmnopqrstuvwxyz12345"
        val encoded = codec.encode(original.encodeToByteArray())
        val decoded = codec.decode(encoded).decodeToString()
        assertEquals(original, decoded)
    }
}
