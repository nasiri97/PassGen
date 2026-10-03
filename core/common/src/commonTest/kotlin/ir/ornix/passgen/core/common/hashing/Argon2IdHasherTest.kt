package ir.ornix.passgen.core.common.hashing

import ir.ornix.passgen.core.common.codec.Base64BinaryCodec
import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import ir.ornix.passgen.core.common.codec.Z85BinaryCodec
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class Argon2IdHasherTest {

    companion object {
        private val base64Codec = Base64BinaryCodec()
        private val z85Codec = Z85BinaryCodec()
        private val hexCodec = HexBinaryCodec(false)

        private val TIMEOUT = 3.minutes
    }

    @Test
    fun `known vectors contain 16 byte salts`() = runTest {
        knownVectors().forEachIndexed { index, knownVector ->
            assertEquals(
                16,
                knownVector.expectedSalt.size,
                "Salt size mismatch in test case $index."
            )
        }
    }

    @Test
    fun `PHC encoded hash contains the expected salt`() = runTest {
        knownVectors().forEachIndexed { index, knownVector ->
            val phcEncoded = knownVector.expectedArgon2idPhcEncoded

            val salt = phcEncoded.substring(
                phcEncoded.indexOf('$', 15) + 1,
                phcEncoded.lastIndexOf('$')
            )

            assertEquals(
                22,
                salt.length,
                "Salt length mismatch in test case $index."
            )

            assertContentEquals(
                knownVector.expectedSalt,
                base64Codec.decode(salt),
                "Salt mismatch in test case $index."
            )
        }
    }

    @Test
    fun `digest with custom salt produces expected digest`() =
        runTest(timeout = TIMEOUT) {
            knownVectors().forEachIndexed { index, knownVector ->
                assertContentEquals(
                    knownVector.expectedArgon2id,
                    Argon2IdHasher.digest(
                        knownVector.inputBytes,
                        knownVector.expectedSalt
                    ),
                    "Digest mismatch in test case $index."
                )
            }
        }

    @Test
    fun `PHC encoded hash is 141 characters`() =
        runTest(timeout = TIMEOUT) {
            knownVectors().forEachIndexed { index, knownVector ->
                assertEquals(
                    141,
                    knownVector.expectedArgon2idPhcEncoded.length,
                    "PHC encoded hash length mismatch in test case $index."
                )
            }
        }

    @Test
    fun `digest produces expected bytes`() =
        runTest(timeout = TIMEOUT) {
            knownVectors().forEachIndexed { index, knownVector ->
                assertContentEquals(
                    knownVector.expectedArgon2id,
                    Argon2IdHasher.digest(knownVector.inputBytes),
                    "Digest mismatch in test case $index."
                )
            }
        }

    @Test
    fun `digest produces expected Base64`() =
        runTest(timeout = TIMEOUT) {
            knownVectors().forEachIndexed { index, knownVector ->
                val phcEncoded = knownVector.expectedArgon2idPhcEncoded

                assertEquals(
                    "${phcEncoded.substring(phcEncoded.lastIndexOf('$') + 1)}==",
                    Argon2IdHasher.digest(
                        knownVector.inputBytes,
                        base64Codec
                    ),
                    "Base64 digest mismatch in test case $index."
                )
            }
        }

    @Test
    fun `digest byte output is 64 bytes`() =
        runTest(timeout = TIMEOUT) {
            knownVectors().forEachIndexed { index, knownVector ->
                assertEquals(
                    64,
                    Argon2IdHasher.digest(knownVector.inputBytes).size,
                    "Digest byte size mismatch in test case $index."
                )
            }
        }

    @Test
    fun `digest Hex output is 128 characters`() =
        runTest(timeout = TIMEOUT) {
            knownVectors().forEachIndexed { index, knownVector ->
                assertEquals(
                    128,
                    Argon2IdHasher.digest(
                        knownVector.inputBytes,
                        hexCodec
                    ).length,
                    "Hex digest length mismatch in test case $index."
                )
            }
        }

    @Test
    fun `digest Base64 output is 88 characters`() =
        runTest(timeout = TIMEOUT) {
            knownVectors().forEachIndexed { index, knownVector ->
                assertEquals(
                    88,
                    Argon2IdHasher.digest(
                        knownVector.inputBytes,
                        base64Codec
                    ).length,
                    "Base64 digest length mismatch in test case $index."
                )
            }
        }

    @Test
    fun `digest Z85 output is 80 characters`() =
        runTest(timeout = TIMEOUT) {
            knownVectors().forEachIndexed { index, knownVector ->
                assertEquals(
                    80,
                    Argon2IdHasher.digest(
                        knownVector.inputBytes,
                        z85Codec
                    ).length,
                    "Z85 digest length mismatch in test case $index."
                )
            }
        }

    private val KnownVector.expectedSalt: ByteArray
        get() = expectedSha256.copyOfRange(0, 16)
}