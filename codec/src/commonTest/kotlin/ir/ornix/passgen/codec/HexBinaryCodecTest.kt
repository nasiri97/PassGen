package ir.ornix.passgen.codec

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HexBinaryCodecTest {

    @Test
    fun testEncodeUppercase() {
        val codec = HexBinaryCodec(uppercaseOutput = true)
        val input = byteArrayOf(0x01.toByte(), 0x0A.toByte(), 0xFF.toByte())
        val expected = "010AFF"
        assertEquals(expected, codec.encode(input))
    }

    @Test
    fun testEncodeLowercase() {
        val codec = HexBinaryCodec(uppercaseOutput = false)
        val input = byteArrayOf(0x01.toByte(), 0x0A.toByte(), 0xFF.toByte())
        val expected = "010aff"
        assertEquals(expected, codec.encode(input))
    }

    @Test
    fun testDecode() {
        val codec = HexBinaryCodec(uppercaseOutput = true)
        val input = "010AFF"
        val expected = byteArrayOf(0x01.toByte(), 0x0A.toByte(), 0xFF.toByte())
        val actual = codec.decode(input)
        assertEquals(expected.size, actual.size)
        for (i in expected.indices) {
            assertEquals(expected[i], actual[i])
        }
    }

    @Test
    fun testDecodeLowercase() {
        val codec = HexBinaryCodec(uppercaseOutput = true)
        val input = "010aff"
        val expected = byteArrayOf(0x01.toByte(), 0x0A.toByte(), 0xFF.toByte())
        val actual = codec.decode(input)
        assertEquals(expected.size, actual.size)
        for (i in expected.indices) {
            assertEquals(expected[i], actual[i])
        }
    }

    @Test
    fun testDecodeInvalidLength() {
        val codec = HexBinaryCodec(uppercaseOutput = true)
        assertFailsWith<IllegalArgumentException> {
            codec.decode("ABC") // Odd length
        }
    }

    @Test
    fun testEncodeInvalidSize() {
        // Hex has a blockSize of 1, so all sizes are valid.
        // We verify that it does NOT throw exceptions for various sizes.
        val codec = HexBinaryCodec(uppercaseOutput = true)
        codec.encode(byteArrayOf(1))
        codec.encode(byteArrayOf(1, 2))
        codec.encode(byteArrayOf(1, 2, 3))
        codec.encode(byteArrayOf())
    }

    @Test
    fun testBlockSizes() {
        val codec = HexBinaryCodec(uppercaseOutput = true)
        assertEquals(1, codec.blockSize)
        assertEquals(2, codec.encodedBlockSize)
    }


    @Test
    fun testAll() {
        val uppercaseCodec = HexBinaryCodec(uppercaseOutput = true)
        val lowercaseCodec = HexBinaryCodec(uppercaseOutput = false)

        val binaryRepresentation1 = "".encodeToByteArray()
        val stringRepresentation1 = ""

        val binaryRepresentation2 = " ".encodeToByteArray()
        val stringRepresentation2 = "20"

        val binaryRepresentation3 = " \n".encodeToByteArray()
        val stringRepresentation3 = "200A"

        val binaryRepresentation4 = "Hello".encodeToByteArray()
        val stringRepresentation4 = "48656C6C6F"

        val binaryRepresentation5 = "Hello  World".encodeToByteArray()
        val stringRepresentation5 = "48656c6c6f2020576f726c64"

        assertEquals(stringRepresentation1, uppercaseCodec.encode(binaryRepresentation1))
        assertEquals(stringRepresentation2, uppercaseCodec.encode(binaryRepresentation2))
        assertEquals(stringRepresentation3, uppercaseCodec.encode(binaryRepresentation3))
        assertEquals(stringRepresentation4, uppercaseCodec.encode(binaryRepresentation4))
        assertEquals(stringRepresentation5, lowercaseCodec.encode(binaryRepresentation5))

        assertContentEquals(binaryRepresentation1, uppercaseCodec.decode(stringRepresentation1))
        assertContentEquals(binaryRepresentation2, uppercaseCodec.decode(stringRepresentation2))
        assertContentEquals(binaryRepresentation3, uppercaseCodec.decode(stringRepresentation3))
        assertContentEquals(binaryRepresentation4, uppercaseCodec.decode(stringRepresentation4))
        assertContentEquals(binaryRepresentation5, lowercaseCodec.decode(stringRepresentation5))
    }
}
