package ir.ornix.passgen.core.common.hashing

import ir.ornix.passgen.core.common.codec.Utf8TextCodec
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Sha256HasherTest {

    private val sha256Hashing = Sha256Hasher()
    private val utf8Codec = Utf8TextCodec()

    @Test
    fun `known vectors produce expected SHA-256 digests`() = runTest {
        knownVectors().forEachIndexed { index, knownVector ->
            assertContentEquals(
                knownVector.expectedSha256,
                sha256Hashing.digest(knownVector.inputBytes),
                "SHA-256 output mismatch in test case $index."
            )
        }
    }

    @Test
    fun `output byte size is 32 bytes`() {
        assertEquals(32, sha256Hashing.outputByteSize)
    }

    @Test
    fun `verify returns true when input matches digest`() = runTest {
        val input = utf8Codec.decode("hello")
        val digest = sha256Hashing.digest(input)

        assertTrue(sha256Hashing.verify(input, digest))
    }

    @Test
    fun `verify returns false when input does not match digest`() = runTest {
        val input = utf8Codec.decode("hello")
        val otherInput = utf8Codec.decode("world")
        val digest = sha256Hashing.digest(input)

        assertFalse(sha256Hashing.verify(otherInput, digest))
    }

    @Test
    fun `verify returns false when digest is modified`() = runTest {
        val input = utf8Codec.decode("hello")
        val digest = sha256Hashing.digest(input)
        digest[0] = (digest[0].toInt() xor 0x01).toByte()

        assertFalse(sha256Hashing.verify(input, digest))
    }

    @Test
    fun `empty input produces a 32 byte digest`() = runTest {
        val digest = sha256Hashing.digest(ByteArray(0))

        assertEquals(32, digest.size)
    }

    @Test
    fun `digest is deterministic`() = runTest {
        val input = utf8Codec.decode("hello")

        val firstDigest = sha256Hashing.digest(input)
        val secondDigest = sha256Hashing.digest(input)

        assertContentEquals(firstDigest, secondDigest)
    }
}
