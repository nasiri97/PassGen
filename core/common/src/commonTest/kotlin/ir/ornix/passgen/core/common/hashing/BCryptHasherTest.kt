package ir.ornix.passgen.core.common.hashing

import ir.ornix.passgen.core.common.codec.BCryptBase64BinaryCodec
import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.minutes

class BCryptHasherTest {

    companion object {
        private val bcryptHasher = BCryptHasher()
        private val bcryptBase64Codec = BCryptBase64BinaryCodec()
        private val hexBinaryCodec = HexBinaryCodec(false)

        private val TIMEOUT = 3.minutes
    }

    @Test
    fun `digest throws exception when input exceeds 72 bytes`() = runTest {
        val input = "a".repeat(73).encodeToByteArray()

        assertFailsWith<IllegalArgumentException> {
            bcryptHasher.digest(input = input)
        }
    }

    @Test
    fun `known vectors contain 16 byte salts`() = runTest(timeout = TIMEOUT) {
        knownVectors().forEachIndexed { index, knownVector ->
            assertEquals(
                16,
                knownVector.expectedSalt.size,
                "Salt size mismatch in test case $index."
            )
        }
    }

    @Test
    fun `Mcf encoded hash contains the expected salt`() = runTest(timeout = TIMEOUT) {
        knownVectors().forEachIndexed { index, knownVector ->
            val encodedHash = knownVector.expectedBcryptMcfEncoded
            val salt = encodedHash.substring(
                encodedHash.lastIndexOf('$') + 1,
                29
            )

            assertEquals(
                22,
                salt.length,
                "Salt length mismatch in test case $index."
            )

            assertContentEquals(
                knownVector.expectedSalt,
                bcryptBase64Codec.decode(salt),
                "Salt mismatch in test case $index."
            )
        }
    }

    @Test
    fun `digest with custom salt produces expected digest`() = runTest {
        knownVectors().forEachIndexed { index, knownVector ->
            assertContentEquals(
                knownVector.expectedBcrypt,
                bcryptHasher.digest(
                    knownVector.inputBytes,
                    knownVector.expectedSalt
                ),
                "Digest mismatch in test case $index."
            )
        }
    }

    @Test
    fun `Mcf encoded hash is 60 characters`() = runTest(timeout = TIMEOUT) {
        knownVectors().forEachIndexed { index, knownVector ->
            assertEquals(
                60,
                knownVector.expectedBcryptMcfEncoded.length,
                "Mcf encoded hash length mismatch in test case $index."
            )
        }
    }

    @Test
    fun `digest produces expected bytes`() = runTest(timeout = TIMEOUT) {
        knownVectors().forEachIndexed { index, knownVector ->
            assertContentEquals(
                knownVector.expectedBcrypt,
                bcryptHasher.digest(knownVector.inputBytes),
                "Digest mismatch in test case $index."
            )
        }
    }

    @Test
    fun `digest produces expected BCrypt Base64`() = runTest(timeout = TIMEOUT) {
        knownVectors().forEachIndexed { index, knownVector ->
            assertEquals(
                knownVector.expectedBcryptMcfEncoded.substring(29),
                bcryptHasher.digest(
                    knownVector.inputBytes,
                    bcryptBase64Codec
                ),
                "Bcrypt Base64 digest mismatch in test case $index."
            )
        }
    }

    @Test
    fun `digest byte output is 23 bytes`() = runTest(timeout = TIMEOUT) {
        knownVectors().forEachIndexed { index, knownVector ->
            assertEquals(
                23,
                bcryptHasher.digest(knownVector.inputBytes).size,
                "Digest byte size mismatch in test case $index."
            )
        }
    }

    @Test
    fun `digest Hex output is 46 characters`() = runTest(timeout = TIMEOUT) {
        knownVectors().forEachIndexed { index, knownVector ->
            assertEquals(
                46,
                bcryptHasher.digest(
                    knownVector.inputBytes,
                    hexBinaryCodec
                ).length,
                "Hex digest length mismatch in test case $index."
            )
        }
    }

    @Test
    fun `digest BCrypt Base64 output is 31 characters`() = runTest(timeout = TIMEOUT) {
        knownVectors().forEachIndexed { index, knownVector ->
            assertEquals(
                31,
                bcryptHasher.digest(
                    knownVector.inputBytes,
                    bcryptBase64Codec
                ).length,
                "Bcrypt Base64 digest length mismatch in test case $index."
            )
        }
    }

    private val KnownVector.expectedSalt: ByteArray
        get() = expectedSha256.copyOfRange(0, 16)
}