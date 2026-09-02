package ir.ornix.passgen.codec

import kotlin.test.Test
import kotlin.test.assertContentEquals
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

    @Test
    fun testAll() {
        val binaryRepresentation1 = "".encodeToByteArray()
        val stringRepresentation1 = ""

        val binaryRepresentation2 = " ".encodeToByteArray()
        val stringRepresentation2 = " "

        val binaryRepresentation3 = " \n".encodeToByteArray()
        val stringRepresentation3 = " \n"

        val binaryRepresentation4 = "Hello".encodeToByteArray()
        val stringRepresentation4 = "Hello"

        val binaryRepresentation5 = "Hello  World".encodeToByteArray()
        val stringRepresentation5 = "Hello  World"

        assertEquals(stringRepresentation1, codec.encode(binaryRepresentation1))
        assertEquals(stringRepresentation2, codec.encode(binaryRepresentation2))
        assertEquals(stringRepresentation3, codec.encode(binaryRepresentation3))
        assertEquals(stringRepresentation4, codec.encode(binaryRepresentation4))
        assertEquals(stringRepresentation5, codec.encode(binaryRepresentation5))

        assertContentEquals(binaryRepresentation1, codec.decode(stringRepresentation1))
        assertContentEquals(binaryRepresentation2, codec.decode(stringRepresentation2))
        assertContentEquals(binaryRepresentation3, codec.decode(stringRepresentation3))
        assertContentEquals(binaryRepresentation4, codec.decode(stringRepresentation4))
        assertContentEquals(binaryRepresentation5, codec.decode(stringRepresentation5))
    }
}
