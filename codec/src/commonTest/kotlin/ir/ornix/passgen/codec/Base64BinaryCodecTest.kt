package ir.ornix.passgen.codec

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Base64BinaryCodecTest {

    private val codec = Base64BinaryCodec()

    @Test
    fun testEncode() {
        val input = byteArrayOf(77, 97, 110) // "Man"
        val expected = "TWFu"
        assertEquals(expected, codec.encode(input))
    }

    @Test
    fun testEncodeMultipleBlocks() {
        val input = "ManMan".encodeToByteArray()
        val expected = "TWFuTWFu"
        assertEquals(expected, codec.encode(input))
    }

    @Test
    fun testEncodeEmpty() {
        assertEquals("", codec.encode(byteArrayOf()))
    }

    @Test
    fun testEncodeInvalidSize() {
        assertFailsWith<IllegalArgumentException> {
            codec.encode(byteArrayOf(77)) // 1 byte, not multiple of 3
        }
        assertFailsWith<IllegalArgumentException> {
            codec.encode(byteArrayOf(77, 97)) // 2 bytes, not multiple of 3
        }
    }

    @Test
    fun testDecode() {
        val input = "TWFu"
        val expected = byteArrayOf(77, 97, 110) // "Man"
        val actual = codec.decode(input)
        assertContentEquals(expected, actual)
    }

    @Test
    fun testDecodeMultipleBlocks() {
        val input = "TWFuTWFu"
        val expected = "ManMan".encodeToByteArray()
        assertContentEquals(expected, codec.decode(input))
    }

    @Test
    fun testDecodeEmpty() {
        assertContentEquals(byteArrayOf(), codec.decode(""))
    }

    @Test
    fun testDecodeInvalidLength() {
        assertFailsWith<IllegalArgumentException> {
            codec.decode("abc") // Length 3, not multiple of 4
        }
        assertFailsWith<IllegalArgumentException> {
            codec.decode("a") // Length 1
        }
    }

    @Test
    fun testDecodeInvalidCharacters() {
        assertFailsWith<IllegalArgumentException> {
            codec.decode("TWF?")
        }
    }

    @Test
    fun testBlockSizes() {
        assertEquals(3, codec.blockSize)
        assertEquals(4, codec.encodedBlockSize)
    }

    @Test
    fun testAll() {
        val binaryRepresentation1 = "".encodeToByteArray()
        val stringRepresentation1 = ""

        val binaryRepresentation2 = "   ".encodeToByteArray()
        val stringRepresentation2 = "ICAg"

        val binaryRepresentation3 = "   \n\n\n".encodeToByteArray()
        val stringRepresentation3 = "ICAgCgoK"

        val binaryRepresentation4 = "Helloo".encodeToByteArray()
        val stringRepresentation4 = "SGVsbG9v"

        val binaryRepresentation5 = "Hello  World".encodeToByteArray()
        val stringRepresentation5 = "SGVsbG8gIFdvcmxk"

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
