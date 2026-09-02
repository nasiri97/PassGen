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
        val str1 = ""
        val encoded1 = ""

        val str2 = "   "
        val encoded2 = "ICAg"

        val str3 = "   \n\n\n"
        val encoded3 = "ICAgCgoK"

        val str4 = "Helloo"
        val encoded4 = "SGVsbG9v"

        val str5 = "Hello  World"
        val encoded5 = "SGVsbG8gIFdvcmxk"

        assertEquals(encoded1, codec.encode(str1.encodeToByteArray()))
        assertEquals(encoded2, codec.encode(str2.encodeToByteArray()))
        assertEquals(encoded3, codec.encode(str3.encodeToByteArray()))
        assertEquals(encoded4, codec.encode(str4.encodeToByteArray()))
        assertEquals(encoded5, codec.encode(str5.encodeToByteArray()))
    }
}
