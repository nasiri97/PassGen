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


    @Test
    fun testAll() {
        val uppercaseCodec = HexBinaryCodec(uppercaseOutput = true)
        val lowercaseCodec = HexBinaryCodec(uppercaseOutput = false)

        val str1 = ""
        val encoded1 = ""

        val str2 = " "
        val encoded2 = "20"

        val str3 = " \n"
        val encoded3 = "200A"

        val str4 = "Hello"
        val encoded4 = "48656C6C6F"

        val str5 = "Hello  World"
        val encoded5 = "48656c6c6f2020576f726c64"

        assertEquals(encoded1, uppercaseCodec.encode(str1.encodeToByteArray()))
        assertEquals(encoded2, uppercaseCodec.encode(str2.encodeToByteArray()))
        assertEquals(encoded3, uppercaseCodec.encode(str3.encodeToByteArray()))
        assertEquals(encoded4, uppercaseCodec.encode(str4.encodeToByteArray()))
        assertEquals(encoded5, lowercaseCodec.encode(str5.encodeToByteArray()))
    }
}
