package ir.ornix.passgen.hashing

import ir.ornix.passgen.codec.HexBinaryCodec
import ir.ornix.passgen.codec.Utf8TextCodec
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Sha256HashingTest {

    private val sha256Hashing = Sha256Hashing()
    private val hexCodec = HexBinaryCodec(false)
    private val utf8Codec = Utf8TextCodec()

    @Test
    fun testOutputByteSize() {
        assertEquals(32, sha256Hashing.outputByteSize)
    }

    @Test
    fun testEmptyString() = runTest {
        val input = ""
        val expected = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        val result = sha256Hashing.digest(utf8Codec.decode(input))
        assertContentEquals(hexCodec.decode(expected), result)
    }

    @Test
    fun testSingleSpace() = runTest {
        val input = " "
        val expected = "36a9e7f1c95b82ffb99743e0c5c4ce95d83c9a430aac59f84ef3cbfab6145068"
        val result = sha256Hashing.digest(utf8Codec.decode(input))
        assertContentEquals(hexCodec.decode(expected), result)
    }

    @Test
    fun testHello() = runTest {
        val input = "hello"
        val expected = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
        val result = sha256Hashing.digest(utf8Codec.decode(input))
        assertContentEquals(hexCodec.decode(expected), result)
    }

    @Test
    fun testVerify() = runTest {
        val input = utf8Codec.decode("hello")
        val digest = sha256Hashing.digest(input)
        assert(sha256Hashing.verify(input, digest))
    }

    @Test
    fun testMultipleSpace() = runTest {
        val input = "  \n\n"
        val expected = "b94f13a5c07a89598ff9c517dbb26d84a29b460a8d2c3671699886e9458ccabf"
        val result = sha256Hashing.digest(utf8Codec.decode(input))
        assertContentEquals(hexCodec.decode(expected), result)
    }

    @Test
    fun testSentence() = runTest {
        val input = "Hello from Hashing\nWe try to hash your content!"
        val expected = "2d589220278b9585b8140665bd70d95d46a9df75c9048542febfcd45c0feddbc"
        val result = sha256Hashing.digest(utf8Codec.decode(input))
        assertContentEquals(hexCodec.decode(expected), result)
    }
}
