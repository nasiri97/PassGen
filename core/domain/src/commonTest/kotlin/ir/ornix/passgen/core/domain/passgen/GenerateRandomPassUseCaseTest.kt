package ir.ornix.passgen.core.domain.passgen

import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.domain.passgenconfig.model.RandomPassGenConfig
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class GenerateRandomPassUseCaseTest {

    private lateinit var useCase: GenerateRandomPassUseCase

    @BeforeTest
    fun setup() {
        useCase = GenerateRandomPassUseCase()
    }

    // -------------------------------------------------------------------------
    // Successful Generation Tests with StringPassEncoders
    // -------------------------------------------------------------------------

    @Test
    fun `invoke with HexPassEncoder config should generate password of exact length with hex chars`() =
        runTest {
            val config = RandomPassGenConfig(
                id = 1,
                name = "Hex Config",
                passEncoder = StringPassEncoder.HexPassEncoder,
                passwordLength = 32
            )
            val hexRegex = Regex("^[0-9a-f]+$")

            val password = useCase(config)

            assertEquals(32, password.length)
            assertTrue(
                hexRegex.matches(password),
                "Password '$password' should contain valid hex characters"
            )
        }

    @Test
    fun `invoke with Base64PassEncoder config should generate password of exact length with Base64 chars`() =
        runTest {
            val config = RandomPassGenConfig(
                id = 2,
                name = "Base64 Config",
                passEncoder = StringPassEncoder.Base64PassEncoder,
                passwordLength = 16
            )
            val base64Regex = Regex("^[A-Za-z0-9+/=]+$")

            val password = useCase(config)

            assertEquals(16, password.length)
            assertTrue(
                base64Regex.matches(password),
                "Password '$password' should contain valid Base64 characters"
            )
        }

    @Test
    fun `invoke with Z85PassEncoder config should generate password of exact specified length`() =
        runTest {
            val config = RandomPassGenConfig(
                id = 3,
                name = "Z85 Config",
                passEncoder = StringPassEncoder.Z85PassEncoder,
                passwordLength = 40
            )

            val password = useCase(config)

            assertEquals(40, password.length)
            assertTrue(password.isNotEmpty())
        }

    @Test
    fun `invoke with maximum valid password length 84 for Base64PassEncoder should succeed`() =
        runTest {
            val config = RandomPassGenConfig(
                id = 4,
                name = "Max Base64 Config",
                passEncoder = StringPassEncoder.Base64PassEncoder,
                passwordLength = 84
            )

            val password = useCase(config)

            assertEquals(84, password.length)
        }

    // -------------------------------------------------------------------------
    // Successful Generation Tests with SeedPassEncoders (BIP-39)
    // -------------------------------------------------------------------------

    @Test
    fun `invoke with Bip39L12PassEncoder config should generate 12 BIP-39 mnemonic words`() =
        runTest {
            verifyBip39SeedEncoderConfig(
                SeedPassEncoder.Bip39L12PassEncoder,
                expectedWordCount = 12
            )
        }

    @Test
    fun `invoke with Bip39L15PassEncoder config should generate 15 BIP-39 mnemonic words`() =
        runTest {
            verifyBip39SeedEncoderConfig(
                SeedPassEncoder.Bip39L15PassEncoder,
                expectedWordCount = 15
            )
        }

    @Test
    fun `invoke with Bip39L18PassEncoder config should generate 18 BIP-39 mnemonic words`() =
        runTest {
            verifyBip39SeedEncoderConfig(
                SeedPassEncoder.Bip39L18PassEncoder,
                expectedWordCount = 18
            )
        }

    @Test
    fun `invoke with Bip39L21PassEncoder config should generate 21 BIP-39 mnemonic words`() =
        runTest {
            verifyBip39SeedEncoderConfig(
                SeedPassEncoder.Bip39L21PassEncoder,
                expectedWordCount = 21
            )
        }

    @Test
    fun `invoke with Bip39L24PassEncoder config should generate 24 BIP-39 mnemonic words`() =
        runTest {
            verifyBip39SeedEncoderConfig(
                SeedPassEncoder.Bip39L24PassEncoder,
                expectedWordCount = 24
            )
        }

    // -------------------------------------------------------------------------
    // Config Validation Tests - StringPassEncoder
    // -------------------------------------------------------------------------

    @Test
    fun `creating RandomPassGenConfig with StringPassEncoder and null passwordLength should throw IllegalArgumentException`() =
        runTest {
            val exception = assertFailsWith<IllegalArgumentException> {
                RandomPassGenConfig(
                    id = 5,
                    name = "Null Length String Config",
                    passEncoder = StringPassEncoder.HexPassEncoder,
                    passwordLength = null
                )
            }
            assertEquals("Password must not be null for StringPassEncoder.", exception.message)
        }

    @Test
    fun `creating RandomPassGenConfig with zero password length should throw IllegalArgumentException`() =
        runTest {
            val exception = assertFailsWith<IllegalArgumentException> {
                RandomPassGenConfig(
                    id = 6,
                    name = "Zero Length Config",
                    passEncoder = StringPassEncoder.Base64PassEncoder,
                    passwordLength = 0
                )
            }
            assertEquals("Password length must be greater than 0.", exception.message)
        }

    @Test
    fun `creating RandomPassGenConfig with negative password length should throw IllegalArgumentException`() =
        runTest {
            val exception = assertFailsWith<IllegalArgumentException> {
                RandomPassGenConfig(
                    id = 7,
                    name = "Negative Length Config",
                    passEncoder = StringPassEncoder.HexPassEncoder,
                    passwordLength = -10
                )
            }
            assertEquals("Password length must be greater than 0.", exception.message)
        }

    @Test
    fun `creating RandomPassGenConfig with password length exceeding max token length should throw IllegalArgumentException`() =
        runTest {
            val exception = assertFailsWith<IllegalArgumentException> {
                RandomPassGenConfig(
                    id = 8,
                    name = "Exceeding Length Config",
                    passEncoder = StringPassEncoder.Base64PassEncoder,
                    passwordLength = 85
                )
            }
            assertEquals(
                exception.message?.contains("Requested Password length must not exceed 84"),
                true,
                "Expected exception message to state max length limit exceeded, got: ${exception.message}"
            )
        }

    // -------------------------------------------------------------------------
    // Config Validation Tests - SeedPassEncoder
    // -------------------------------------------------------------------------

    @Test
    fun `creating RandomPassGenConfig with SeedPassEncoder and non-null passwordLength should throw IllegalArgumentException`() =
        runTest {
            val seedEncoders = listOf(
                SeedPassEncoder.Bip39L12PassEncoder,
                SeedPassEncoder.Bip39L15PassEncoder,
                SeedPassEncoder.Bip39L18PassEncoder,
                SeedPassEncoder.Bip39L21PassEncoder,
                SeedPassEncoder.Bip39L24PassEncoder
            )

            for (encoder in seedEncoders) {
                val exception = assertFailsWith<IllegalArgumentException> {
                    RandomPassGenConfig(
                        id = 9,
                        name = "Invalid Seed Config",
                        passEncoder = encoder,
                        passwordLength = 12
                    )
                }
                assertEquals("Password must be null for SeedPassEncoder.", exception.message)
            }
        }

    // -------------------------------------------------------------------------
    // Randomness & Config Variety Tests
    // -------------------------------------------------------------------------

    @Test
    fun `consecutive invoke calls with same StringPassEncoder config should produce distinct random passwords`() =
        runTest {
            val config = RandomPassGenConfig(
                id = 10,
                name = "Random Test Config",
                passEncoder = StringPassEncoder.Base64PassEncoder,
                passwordLength = 32
            )

            val pass1 = useCase(config)
            val pass2 = useCase(config)
            val pass3 = useCase(config)

            assertNotEquals(pass1, pass2, "Consecutive calls should produce distinct passwords")
            assertNotEquals(pass2, pass3, "Consecutive calls should produce distinct passwords")
            assertNotEquals(pass1, pass3, "Consecutive calls should produce distinct passwords")
        }

    @Test
    fun `consecutive invoke calls with same SeedPassEncoder config should produce distinct mnemonic phrases`() =
        runTest {
            val config = RandomPassGenConfig(
                id = 11,
                name = "Random Seed Config",
                passEncoder = SeedPassEncoder.Bip39L12PassEncoder,
                passwordLength = null
            )

            val pass1 = useCase(config)
            val pass2 = useCase(config)

            assertNotEquals(
                pass1,
                pass2,
                "Consecutive seed calls should produce distinct mnemonics"
            )
        }

    @Test
    fun `invoke with different configs should produce passwords matching their respective configurations`() =
        runTest {
            val hexConfig = RandomPassGenConfig(
                id = 12,
                name = "Hex 20",
                passEncoder = StringPassEncoder.HexPassEncoder,
                passwordLength = 20
            )
            val base64Config = RandomPassGenConfig(
                id = 13,
                name = "Base64 50",
                passEncoder = StringPassEncoder.Base64PassEncoder,
                passwordLength = 50
            )

            val hexPassword = useCase(hexConfig)
            val base64Password = useCase(base64Config)

            assertEquals(20, hexPassword.length)
            assertEquals(50, base64Password.length)
            assertNotEquals(hexPassword, base64Password)
        }

    // -------------------------------------------------------------------------
    // Helper Methods
    // -------------------------------------------------------------------------

    private suspend fun verifyBip39SeedEncoderConfig(
        encoder: SeedPassEncoder,
        expectedWordCount: Int
    ) {
        val config = RandomPassGenConfig(
            id = 100,
            name = "BIP39 Config",
            passEncoder = encoder,
            passwordLength = null
        )

        val password = useCase(config)

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
