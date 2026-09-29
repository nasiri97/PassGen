package ir.ornix.passgen.core.common.passwordgenerator.random

import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class RandomPassGenTest {

    // -------------------------------------------------------------------------
    // Properties & Initialization Tests
    // -------------------------------------------------------------------------

    @Test
    fun `maxEntropyByteSize should equal constant value 64`() {
        val generator = RandomPassGen(
            passEncoder = StringPassEncoder.HexPassEncoder,
            passwordLength = 16
        )

        assertEquals(64, generator.maxEntropyByteSize)
        assertEquals(
            RandomPassGen.RANDOM_PASS_GEN_MAX_ENTROPY_BYTE_SIZE,
            generator.maxEntropyByteSize
        )
    }

    @Test
    fun `properties should be correctly assigned upon initialization`() {
        val encoder = StringPassEncoder.Base64PassEncoder
        val length = 32

        val generator = RandomPassGen(
            passEncoder = encoder,
            passwordLength = length
        )

        assertEquals(encoder, generator.passEncoder)
        assertEquals(length, generator.passwordLength)
    }

    // -------------------------------------------------------------------------
    // Validation Tests - StringPassEncoder (Valid cases)
    // -------------------------------------------------------------------------

    @Test
    fun `HexPassEncoder should allow passwordLength from 1 up to maximum token length 128`() {
        // Min valid length
        val minGen = RandomPassGen(StringPassEncoder.HexPassEncoder, passwordLength = 1)
        assertEquals(1, minGen.passwordLength)

        // Typical valid length
        val midGen = RandomPassGen(StringPassEncoder.HexPassEncoder, passwordLength = 64)
        assertEquals(64, midGen.passwordLength)

        // Max valid length (64 bytes * 2 hex chars / byte = 128)
        val maxGen = RandomPassGen(StringPassEncoder.HexPassEncoder, passwordLength = 128)
        assertEquals(128, maxGen.passwordLength)
    }

    @Test
    fun `Base64PassEncoder should allow passwordLength from 1 up to maximum token length 84`() {
        // Min valid length
        val minGen = RandomPassGen(StringPassEncoder.Base64PassEncoder, passwordLength = 1)
        assertEquals(1, minGen.passwordLength)

        // Typical valid length
        val midGen = RandomPassGen(StringPassEncoder.Base64PassEncoder, passwordLength = 32)
        assertEquals(32, midGen.passwordLength)

        // Max valid length ((64 / 3) * 4 = 84)
        val maxGen = RandomPassGen(StringPassEncoder.Base64PassEncoder, passwordLength = 84)
        assertEquals(84, maxGen.passwordLength)
    }

    @Test
    fun `Z85PassEncoder should allow passwordLength from 1 up to maximum token length 80`() {
        // Min valid length
        val minGen = RandomPassGen(StringPassEncoder.Z85PassEncoder, passwordLength = 1)
        assertEquals(1, minGen.passwordLength)

        // Typical valid length
        val midGen = RandomPassGen(StringPassEncoder.Z85PassEncoder, passwordLength = 40)
        assertEquals(40, midGen.passwordLength)

        // Max valid length ((64 / 4) * 5 = 80)
        val maxGen = RandomPassGen(StringPassEncoder.Z85PassEncoder, passwordLength = 80)
        assertEquals(80, maxGen.passwordLength)
    }

    // -------------------------------------------------------------------------
    // Validation Tests - StringPassEncoder (Invalid cases)
    // -------------------------------------------------------------------------

    @Test
    fun `StringPassEncoder with null passwordLength should throw IllegalArgumentException`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            RandomPassGen(
                passEncoder = StringPassEncoder.HexPassEncoder,
                passwordLength = null
            )
        }
        assertEquals("Password must not be null for StringPassEncoder.", exception.message)
    }

    @Test
    fun `StringPassEncoder with zero passwordLength should throw IllegalArgumentException`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            RandomPassGen(
                passEncoder = StringPassEncoder.HexPassEncoder,
                passwordLength = 0
            )
        }
        assertEquals("Password length must be greater than 0.", exception.message)
    }

    @Test
    fun `StringPassEncoder with negative passwordLength should throw IllegalArgumentException`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            RandomPassGen(
                passEncoder = StringPassEncoder.HexPassEncoder,
                passwordLength = -10
            )
        }
        assertEquals("Password length must be greater than 0.", exception.message)
    }

    @Test
    fun `HexPassEncoder with passwordLength exceeding 128 should throw IllegalArgumentException`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            RandomPassGen(
                passEncoder = StringPassEncoder.HexPassEncoder,
                passwordLength = 129
            )
        }
        assertEquals(
            exception.message?.contains("Requested Password length must not exceed 128"),
            true,
            "Expected exception message to contain token length limit error, got: ${exception.message}"
        )
    }

    @Test
    fun `Base64PassEncoder with passwordLength exceeding 84 should throw IllegalArgumentException`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            RandomPassGen(
                passEncoder = StringPassEncoder.Base64PassEncoder,
                passwordLength = 85
            )
        }
        assertEquals(
            exception.message?.contains("Requested Password length must not exceed 84"),
            true,
            "Expected exception message to contain token length limit error, got: ${exception.message}"
        )
    }

    @Test
    fun `Z85PassEncoder with passwordLength exceeding 80 should throw IllegalArgumentException`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            RandomPassGen(
                passEncoder = StringPassEncoder.Z85PassEncoder,
                passwordLength = 81
            )
        }
        assertEquals(
            exception.message?.contains("Requested Password length must not exceed 80"),
            true,
            "Expected exception message to contain token length limit error, got: ${exception.message}"
        )
    }

    // -------------------------------------------------------------------------
    // Validation Tests - SeedPassEncoder
    // -------------------------------------------------------------------------

    @Test
    fun `SeedPassEncoder with null passwordLength should initialize successfully`() {
        val seedEncoders = listOf(
            SeedPassEncoder.Bip39L12PassEncoder,
            SeedPassEncoder.Bip39L15PassEncoder,
            SeedPassEncoder.Bip39L18PassEncoder,
            SeedPassEncoder.Bip39L21PassEncoder,
            SeedPassEncoder.Bip39L24PassEncoder
        )

        for (encoder in seedEncoders) {
            val generator = RandomPassGen(
                passEncoder = encoder,
                passwordLength = null
            )
            assertEquals(encoder, generator.passEncoder)
            assertEquals(null, generator.passwordLength)
        }
    }

    @Test
    fun `SeedPassEncoder with non-null passwordLength should throw IllegalArgumentException`() {
        val seedEncoders = listOf(
            SeedPassEncoder.Bip39L12PassEncoder,
            SeedPassEncoder.Bip39L15PassEncoder,
            SeedPassEncoder.Bip39L18PassEncoder,
            SeedPassEncoder.Bip39L21PassEncoder,
            SeedPassEncoder.Bip39L24PassEncoder
        )

        for (encoder in seedEncoders) {
            val exception = assertFailsWith<IllegalArgumentException> {
                RandomPassGen(
                    passEncoder = encoder,
                    passwordLength = 12
                )
            }
            assertEquals("Password must be null for SeedPassEncoder.", exception.message)
        }
    }

    // -------------------------------------------------------------------------
    // Password Generation Tests - StringPassEncoder
    // -------------------------------------------------------------------------

    @Test
    fun `generate with HexPassEncoder should produce string of exact requested length with hex chars`() =
        runTest {
            val lengths = listOf(1, 16, 32, 64, 128)
            val hexRegex = Regex("^[0-9a-f]+$")

            for (len in lengths) {
                val generator = RandomPassGen(
                    passEncoder = StringPassEncoder.HexPassEncoder,
                    passwordLength = len
                )
                val password = generator.generate()

                assertEquals(len, password.length, "Hex password length mismatch for length $len")
                assertTrue(
                    hexRegex.matches(password),
                    "Hex password '$password' contains invalid characters"
                )
            }
        }

    @Test
    fun `generate with Base64PassEncoder should produce string of exact requested length`() =
        runTest {
            val lengths = listOf(1, 12, 32, 64, 84)
            val base64Regex = Regex("^[A-Za-z0-9+/=]+$")

            for (len in lengths) {
                val generator = RandomPassGen(
                    passEncoder = StringPassEncoder.Base64PassEncoder,
                    passwordLength = len
                )
                val password = generator.generate()

                assertEquals(
                    len,
                    password.length,
                    "Base64 password length mismatch for length $len"
                )
                assertTrue(
                    base64Regex.matches(password),
                    "Base64 password '$password' contains invalid characters"
                )
            }
        }

    @Test
    fun `generate with Z85PassEncoder should produce string of exact requested length`() = runTest {
        val lengths = listOf(1, 15, 32, 64, 80)

        for (len in lengths) {
            val generator = RandomPassGen(
                passEncoder = StringPassEncoder.Z85PassEncoder,
                passwordLength = len
            )
            val password = generator.generate()

            assertEquals(len, password.length, "Z85 password length mismatch for length $len")
            assertTrue(password.isNotEmpty(), "Z85 password should not be empty")
        }
    }

    // -------------------------------------------------------------------------
    // Password Generation Tests - SeedPassEncoder (BIP-39)
    // -------------------------------------------------------------------------

    @Test
    fun `generate with Bip39L12PassEncoder should return 12 words`() = runTest {
        verifyBip39WordCount(SeedPassEncoder.Bip39L12PassEncoder, expectedWordCount = 12)
    }

    @Test
    fun `generate with Bip39L15PassEncoder should return 15 words`() = runTest {
        verifyBip39WordCount(SeedPassEncoder.Bip39L15PassEncoder, expectedWordCount = 15)
    }

    @Test
    fun `generate with Bip39L18PassEncoder should return 18 words`() = runTest {
        verifyBip39WordCount(SeedPassEncoder.Bip39L18PassEncoder, expectedWordCount = 18)
    }

    @Test
    fun `generate with Bip39L21PassEncoder should return 21 words`() = runTest {
        verifyBip39WordCount(SeedPassEncoder.Bip39L21PassEncoder, expectedWordCount = 21)
    }

    @Test
    fun `generate with Bip39L24PassEncoder should return 24 words`() = runTest {
        verifyBip39WordCount(SeedPassEncoder.Bip39L24PassEncoder, expectedWordCount = 24)
    }

    // -------------------------------------------------------------------------
    // Randomness & Uniqueness Tests
    // -------------------------------------------------------------------------

    @Test
    fun `consecutive generate calls should produce distinct passwords`() = runTest {
        val generator = RandomPassGen(
            passEncoder = StringPassEncoder.HexPassEncoder,
            passwordLength = 32
        )

        val pass1 = generator.generate()
        val pass2 = generator.generate()
        val pass3 = generator.generate()

        assertNotEquals(
            pass1,
            pass2,
            "Consecutive password generations should produce different passwords"
        )
        assertNotEquals(
            pass2,
            pass3,
            "Consecutive password generations should produce different passwords"
        )
        assertNotEquals(
            pass1,
            pass3,
            "Consecutive password generations should produce different passwords"
        )
    }

    @Test
    fun `consecutive generate calls for Bip39 seed encoder should produce distinct word sets`() =
        runTest {
            val generator = RandomPassGen(
                passEncoder = SeedPassEncoder.Bip39L12PassEncoder,
                passwordLength = null
            )

            val pass1 = generator.generate()
            val pass2 = generator.generate()

            assertNotEquals(
                pass1,
                pass2,
                "Consecutive BIP39 generations should produce different word sets"
            )
        }

    // -------------------------------------------------------------------------
    // Helper Methods
    // -------------------------------------------------------------------------

    private suspend fun verifyBip39WordCount(encoder: SeedPassEncoder, expectedWordCount: Int) {
        val generator = RandomPassGen(passEncoder = encoder, passwordLength = null)
        val password = generator.generate()

        val words = password.trim().split(Regex("\\s+"))
        assertEquals(
            expectedWordCount,
            words.size,
            "Expected $expectedWordCount words for ${encoder.key}, got ${words.size}: '$password'"
        )
        assertTrue(
            words.all { word -> word.isNotEmpty() && word.all { char -> char in 'a'..'z' } },
            "All words in BIP-39 mnemonic must be non-empty lowercase string"
        )
    }
}
