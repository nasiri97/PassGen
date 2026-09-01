package ir.ornix.passgen.codec

import kotlin.test.Test
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
}
