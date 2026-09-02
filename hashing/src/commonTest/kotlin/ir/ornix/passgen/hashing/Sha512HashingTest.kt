package ir.ornix.passgen.hashing

import ir.ornix.passgen.codec.HexBinaryCodec
import ir.ornix.passgen.codec.Utf8TextCodec
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Sha512HashingTest {

    private val sha512Hashing = Sha512Hashing()
    private val hexCodec = HexBinaryCodec(false)
    private val utf8Codec = Utf8TextCodec()

    @Test
    fun testOutputByteSize() {
        assertEquals(64, sha512Hashing.outputByteSize)
    }

    @Test
    fun testEmptyString() = runTest {
        val input = ""
        val expected =
            "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"
        val result = sha512Hashing.digest(utf8Codec.decode(input))
        assertContentEquals(hexCodec.decode(expected), result)
    }

    @Test
    fun testSingleSpace() = runTest {
        val input = " "
        val expected =
            "f90ddd77e400dfe6a3fcf479b00b1ee29e7015c5bb8cd70f5f15b4886cc339275ff553fc8a053f8ddc7324f45168cffaf81f8c3ac93996f6536eef38e5e40768"
        val result = sha512Hashing.digest(utf8Codec.decode(input))
        assertContentEquals(hexCodec.decode(expected), result)
    }

    @Test
    fun testHello() = runTest {
        val input = "hello"
        val expected =
            "9b71d224bd62f3785d96d46ad3ea3d73319bfbc2890caadae2dff72519673ca72323c3d99ba5c11d7c7acc6e14b8c5da0c4663475c2e5c3adef46f73bcdec043"
        val result = sha512Hashing.digest(utf8Codec.decode(input))
        assertContentEquals(hexCodec.decode(expected), result)
    }

    @Test
    fun testVerify() = runTest {
        val input = utf8Codec.decode("hello")
        val digest = sha512Hashing.digest(input)
        assert(sha512Hashing.verify(input, digest))
    }

    @Test
    fun testMultipleSpace() = runTest {
        val input = "  \n\n"
        val expected =
            "75efcb83616f0edce2b49ead6126d0596226c2f89dad6e7b2a6bc05c18613f055ebac846c297d28a2e87aca417651e604a32580d9d5fd091ba19e792acc5618a"
        val result = sha512Hashing.digest(utf8Codec.decode(input))
        assertContentEquals(hexCodec.decode(expected), result)
    }

    @Test
    fun testSentence() = runTest {
        val input = "Hello from Hashing\nWe try to hash your content!"
        val expected =
            "bd2bdac92d5902008cd7473f669d30c32de7c1348d4c340f96bb97ee14c43c0de19f0d3b3f93f014a40285bb4bb6b4eec5214dc82396e0bb05fe36429c93a739"
        val result = sha512Hashing.digest(utf8Codec.decode(input))
        assertContentEquals(hexCodec.decode(expected), result)
    }
}