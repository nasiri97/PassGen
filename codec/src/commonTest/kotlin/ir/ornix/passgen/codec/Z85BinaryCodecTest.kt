package ir.ornix.passgen.codec

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.random.Random

class Z85BinaryCodecTest {

    private val codec = Z85BinaryCodec()

    @Test
    fun testSimpleCases() {
        val cases = mapOf(
            byteArrayOf(0, 0, 0, 0) to "00000",
            byteArrayOf(0, 0, 0, 1) to "00001",
            byteArrayOf(0, 0, 1, 0) to "00031",
            byteArrayOf(0, 1, 0, 0) to "00961",
            byteArrayOf(1, 0, 0, 0) to "0rr91"
        )

        for ((bytes, expected) in cases) {
            assertEquals(expected, codec.encode(bytes), "Failed encoding ${bytes.contentToString()}")
            val decoded = codec.decode(expected)
            assertEquals(bytes.size, decoded.size, "Decoded size mismatch for $expected")
            for (i in bytes.indices) {
                assertEquals(bytes[i], decoded[i], "Failed decoding $expected at index $i")
            }
        }
    }

    @Test
    fun testRoundTripRandom() {
        val random = Random(42)
        repeat(100) {
            // Z85 in this implementation requires multiple of 4 bytes
            val bytes = ByteArray(40) { random.nextInt().toByte() }
            val encoded = codec.encode(bytes)
            val decoded = codec.decode(encoded)
            assertEquals(bytes.size, decoded.size)
            for (i in bytes.indices) {
                assertEquals(bytes[i], decoded[i])
            }
        }
    }

    @Test
    fun testEncodeInvalidSize() {
        // Z85 processes 4-byte blocks. Any other size should fail.
        assertFailsWith<IllegalArgumentException> {
            codec.encode(byteArrayOf(1))
        }
        assertFailsWith<IllegalArgumentException> {
            codec.encode(byteArrayOf(1, 2))
        }
        assertFailsWith<IllegalArgumentException> {
            codec.encode(byteArrayOf(1, 2, 3))
        }
    }

    @Test
    fun testDecodeInvalidStringLength() {
        assertFailsWith<IllegalArgumentException> {
            codec.decode("Hell") // Length 4, not multiple of 5
        }
    }

    @Test
    fun testDecodeInvalidCharacters() {
        assertFailsWith<IllegalArgumentException> {
            codec.decode("abc i") // space is not in ALPHABET
        }
    }

    @Test
    fun testBlockSizes() {
        assertEquals(4, codec.blockSize)
        assertEquals(5, codec.encodedBlockSize)
    }

    @Test
    fun testAll() {
        val str1 = ""
        val encoded1 = ""

        val str2 = "    "
        val encoded2 = "arR^H"

        val str3 = "    \n\n\n\n"
        val encoded3 = "arR^H3jmaE"

        val str4 = "Hell"
        val encoded4 = "nm=QN"

        val str5 = "Hello  World"
        val encoded5 = "nm=QNzY*f7z/PV8"

        assertEquals(encoded1, codec.encode(str1.encodeToByteArray()))
        assertEquals(encoded2, codec.encode(str2.encodeToByteArray()))
        assertEquals(encoded3, codec.encode(str3.encodeToByteArray()))
        assertEquals(encoded4, codec.encode(str4.encodeToByteArray()))
        assertEquals(encoded5, codec.encode(str5.encodeToByteArray()))
    }
}
