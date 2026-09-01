package ir.ornix.passgen.codec

import kotlin.test.Test
import kotlin.test.assertEquals

class Utf8TextCodecTest {

    private val codec = Utf8TextCodec()

    @Test
    fun testEncode() {
        val input = byteArrayOf(72, 101, 108, 108, 111) // "Hello"
        val expected = "Hello"
        assertEquals(expected, codec.encode(input))
    }

    @Test
    fun testDecode() {
        val input = "Hello"
        val expected = byteArrayOf(72, 101, 108, 108, 111)
        val actual = codec.decode(input)
        assertEquals(expected.size, actual.size)
        for (i in expected.indices) {
            assertEquals(expected[i], actual[i])
        }
    }

    @Test
    fun testEncodeUtf8() {
        val input = "سلام".encodeToByteArray()
        val encoded = codec.encode(input)
        assertEquals("سلام", encoded)
    }

    @Test
    fun testEncodeInvalidSize() {
        // UTF-8 is not a fixed-block codec, so all sizes are valid.
        codec.encode(byteArrayOf(1))
        codec.encode(byteArrayOf(1, 2))
        codec.encode(byteArrayOf(1, 2, 3))
        codec.encode(byteArrayOf())
    }
}
