package ir.ornix.passgen.core.common.hashing

import ir.ornix.passgen.core.common.codec.Utf8TextCodec
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Sha512HasherTest {

    private val sha512Hashing = Sha512Hasher()
    private val utf8Codec = Utf8TextCodec()

    @Test
    fun `known vectors produce expected SHA-512 digests`() = runTest {
        knownVectors().forEachIndexed { index, knownVector ->
            assertContentEquals(
                knownVector.expectedSha512,
                sha512Hashing.digest(knownVector.inputBytes),
                "SHA-512 output mismatch in test case $index."
            )
        }
    }

    @Test
    fun `output byte size is 64 bytes`() {
        assertEquals(64, sha512Hashing.outputByteSize)
    }

    @Test
    fun `verify returns true when input matches digest`() = runTest {
        val input = utf8Codec.decode("hello")
        val digest = sha512Hashing.digest(input)

        assertTrue(sha512Hashing.verify(input, digest))
    }

    @Test
    fun `verify returns false when input does not match digest`() = runTest {
        val input = utf8Codec.decode("hello")
        val otherInput = utf8Codec.decode("world")
        val digest = sha512Hashing.digest(input)

        assertFalse(sha512Hashing.verify(otherInput, digest))
    }

    @Test
    fun `verify returns false when digest is modified`() = runTest {
        val input = utf8Codec.decode("hello")
        val digest = sha512Hashing.digest(input)
        digest[0] = (digest[0].toInt() xor 0x01).toByte()

        assertFalse(sha512Hashing.verify(input, digest))
    }

    @Test
    fun `empty input produces a 64 byte digest`() = runTest {
        val digest = sha512Hashing.digest(ByteArray(0))

        assertEquals(64, digest.size)
    }

    @Test
    fun `digest is deterministic`() = runTest {
        val input = utf8Codec.decode("hello")

        val firstDigest = sha512Hashing.digest(input)
        val secondDigest = sha512Hashing.digest(input)

        assertContentEquals(firstDigest, secondDigest)
    }
}