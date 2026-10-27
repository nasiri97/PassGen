package ir.ornix.passgen.core.common.passwordgenerator.kdf

import ir.ornix.passgen.core.common.codec.Utf8TextCodec
import ir.ornix.passgen.core.common.passwordgenerator.model.IllegalPasswordLengthException
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.minutes

class KDFPassGenTest {

    private val utf8Codec = Utf8TextCodec()

    @Test
    fun `password length above maximum throws exception`() = runTest {
        InputHasher.allItems.forEach { inputHasher ->
            StringPassEncoder.allStringPassEncoders.forEach { passEncoder ->
                val maxLength =
                    (inputHasher.outputByteSize / passEncoder.binaryBlockSize) *
                            passEncoder.encodedBlockSize

                assertFailsWith<IllegalArgumentException> {
                    KDFPassGen(
                        inputHasher = inputHasher,
                        passEncoder = passEncoder,
                        passwordLength = maxLength + 1
                    )
                }
            }
        }
    }

    @Test
    fun `maximum password length produces expected result`() = runTest {
        InputHasher.allItems.forEach { inputHasher ->
            StringPassEncoder.allStringPassEncoders.forEach { passEncoder ->
                val maxLength =
                    (inputHasher.outputByteSize / passEncoder.binaryBlockSize) * passEncoder.encodedBlockSize

                knownVectors().forEach { testCase ->
                    val expected = testCase.getExpectedDigest(
                        inputHasher = inputHasher,
                        passEncoder = passEncoder,
                        length = null
                    )

                    assertEquals(
                        expected,
                        KDFPassGen(
                            inputHasher = inputHasher,
                            passEncoder = passEncoder,
                            passwordLength = maxLength
                        ).generate(
                            input = testCase.input,
                            inputDecoder = utf8Codec
                        )
                    )
                }
            }
        }
    }

    @Test
    fun `custom password lengths produce expected results`() =
        runTest(timeout = 5.minutes) {
            InputHasher.allItems.forEach { inputHasher ->
                StringPassEncoder.allStringPassEncoders.forEach { passEncoder ->
                    knownVectors().forEach { testCase ->
                        val maxLength =
                            (inputHasher.outputByteSize / passEncoder.binaryBlockSize) * passEncoder.encodedBlockSize

                        (maxLength - 5..maxLength).forEach { length ->
                            val expected = testCase.getExpectedDigest(
                                inputHasher = inputHasher,
                                passEncoder = passEncoder,
                                length = length
                            )

                            assertEquals(
                                expected,
                                KDFPassGen(
                                    inputHasher = inputHasher,
                                    passEncoder = passEncoder,
                                    passwordLength = length
                                ).generate(
                                    input = testCase.input,
                                    inputDecoder = utf8Codec
                                )
                            )
                        }
                    }
                }
            }
        }

    // -------------------------------------------------------------------------
    // Property Tests
    // -------------------------------------------------------------------------

    @Test
    fun `maximum entropy byte size matches input hasher output byte size`() {
        InputHasher.allItems.forEach { inputHasher ->
            val passGen = KDFPassGen(
                inputHasher = inputHasher,
                passEncoder = StringPassEncoder.HexPassEncoder,
                passwordLength = 10
            )

            assertEquals(
                inputHasher.outputByteSize,
                passGen.maxEntropyByteSize,
                "maxEntropyByteSize should equal inputHasher.outputByteSize for ${inputHasher.key}"
            )
        }
    }

    // -------------------------------------------------------------------------
    // StringPassEncoder Validation Tests
    // -------------------------------------------------------------------------

    @Test
    fun `StringPassEncoder with null password length throws exception`() {
        InputHasher.allItems.forEach { inputHasher ->
            StringPassEncoder.allStringPassEncoders.forEach { passEncoder ->
                assertFailsWith<IllegalPasswordLengthException> {
                    KDFPassGen(
                        inputHasher = inputHasher,
                        passEncoder = passEncoder,
                        passwordLength = null
                    )
                }
            }
        }
    }

    @Test
    fun `StringPassEncoder with zero password length throws exception`() {
        InputHasher.allItems.forEach { inputHasher ->
            StringPassEncoder.allStringPassEncoders.forEach { passEncoder ->
                assertFailsWith<IllegalPasswordLengthException> {
                    KDFPassGen(
                        inputHasher = inputHasher,
                        passEncoder = passEncoder,
                        passwordLength = 0
                    )
                }
            }
        }
    }

    @Test
    fun `StringPassEncoder with negative password length throws exception`() {
        InputHasher.allItems.forEach { inputHasher ->
            StringPassEncoder.allStringPassEncoders.forEach { passEncoder ->
                assertFailsWith<IllegalPasswordLengthException> {
                    KDFPassGen(
                        inputHasher = inputHasher,
                        passEncoder = passEncoder,
                        passwordLength = -5
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // SeedPassEncoder Validation Tests
    // -------------------------------------------------------------------------

    @Test
    fun `SeedPassEncoder with non-null password length throws exception`() {
        InputHasher.allItems.forEach { inputHasher ->
            val validSeedEncoders = PassEncoder
                .getValidItems(inputHasher.outputByteSize)
                .filterIsInstance<SeedPassEncoder>()

            validSeedEncoders.forEach { seedEncoder ->
                assertFailsWith<IllegalPasswordLengthException> {
                    KDFPassGen(
                        inputHasher = inputHasher,
                        passEncoder = seedEncoder,
                        passwordLength = 12
                    )
                }
            }
        }
    }

    @Test
    fun `SeedPassEncoder with null password length initializes successfully`() {
        val validSeedEncoders = PassEncoder
            .getValidItems(InputHasher.SHA512.outputByteSize)
            .filterIsInstance<SeedPassEncoder>()

        validSeedEncoders.forEach { seedEncoder ->
            val passGen = KDFPassGen(
                inputHasher = InputHasher.SHA512,
                passEncoder = seedEncoder,
                passwordLength = null
            )

            assertEquals(seedEncoder, passGen.passEncoder)
            assertEquals(null, passGen.passwordLength)
        }
    }

    // -------------------------------------------------------------------------
    // SeedPassEncoder Password Generation Tests (BIP-39)
    // -------------------------------------------------------------------------

    @Test
    fun `SeedPassEncoder produces deterministic BIP-39 mnemonics`() = runTest {
        val seedEncodersAndWordCounts = listOf(
            SeedPassEncoder.Bip39L12PassEncoder to 12,
            SeedPassEncoder.Bip39L15PassEncoder to 15,
            SeedPassEncoder.Bip39L18PassEncoder to 18,
            SeedPassEncoder.Bip39L21PassEncoder to 21,
            SeedPassEncoder.Bip39L24PassEncoder to 24
        )

        for ((seedEncoder, expectedWordCount) in seedEncodersAndWordCounts) {
            val passGen = KDFPassGen(
                inputHasher = InputHasher.SHA512,
                passEncoder = seedEncoder,
                passwordLength = null
            )

            val resultString1 =
                passGen.generate("my_secret_seed_phrase_input", utf8Codec)
            val resultString2 =
                passGen.generate("my_secret_seed_phrase_input", utf8Codec)

            assertNotNull(resultString1)
            assertEquals(resultString1, resultString2)

            val words = resultString1.trim().split(Regex("\\s+"))

            assertEquals(
                expectedWordCount,
                words.size,
                "Expected $expectedWordCount words for ${seedEncoder.key}"
            )

            val byteArrayInput =
                utf8Codec.decode("my_secret_seed_phrase_input")
            val resultBytes = passGen.generate(byteArrayInput)

            assertEquals(resultString1, resultBytes)
        }
    }

    // -------------------------------------------------------------------------
    // Direct ByteArray Input & Decoder Parity Tests
    // -------------------------------------------------------------------------

    @Test
    fun `ByteArray input produces the same result as String input with decoder`() =
        runTest {
            val passGen = KDFPassGen(
                inputHasher = InputHasher.SHA256,
                passEncoder = StringPassEncoder.HexPassEncoder,
                passwordLength = 32
            )

            val inputString = "Test Input String 123!"
            val inputBytes = utf8Codec.decode(inputString)

            val passFromString = passGen.generate(inputString, utf8Codec)
            val passFromBytes = passGen.generate(inputBytes)

            assertNotNull(passFromString)
            assertEquals(passFromString, passFromBytes)
        }

    @Test
    fun `empty ByteArray input produces a deterministic password`() = runTest {
        val passGen = KDFPassGen(
            inputHasher = InputHasher.SHA256,
            passEncoder = StringPassEncoder.Base64PassEncoder,
            passwordLength = 16
        )

        val emptyBytes = ByteArray(0)
        val pass1 = passGen.generate(emptyBytes)
        val pass2 = passGen.generate(emptyBytes)

        assertNotNull(pass1)
        assertEquals(16, pass1.length)
        assertEquals(pass1, pass2)
    }

    // -------------------------------------------------------------------------
    // Caching & Input Sensitivity Tests
    // -------------------------------------------------------------------------

    @Test
    fun `same input produces the same password and different input produces a different password`() =
        runTest {
            val passGen = KDFPassGen(
                inputHasher = InputHasher.SHA256,
                passEncoder = StringPassEncoder.HexPassEncoder,
                passwordLength = 32
            )

            val input1 = "secret_password_1".encodeToByteArray()
            val input2 = "secret_password_2".encodeToByteArray()

            val result1a = passGen.generate(input1)
            val result1b = passGen.generate(input1)
            val result2 = passGen.generate(input2)
            val result1c = passGen.generate(input1)

            assertNotNull(result1a)
            assertNotNull(result1b)
            assertNotNull(result2)
            assertNotNull(result1c)

            assertEquals(result1a, result1b)
            assertNotEquals(result1a, result2)
            assertEquals(result1a, result1c)
        }

    // -------------------------------------------------------------------------
    // Valid Combinations Tests
    // -------------------------------------------------------------------------

    @Test
    fun `all valid hasher and encoder combinations produce a password`() =
        runTest {
            for (hasher in InputHasher.allItems) {
                val validEncoders = PassEncoder.getValidItems(hasher)

                for (encoder in validEncoders) {
                    val passLen = when (encoder) {
                        is StringPassEncoder ->
                            encoder.getTokenLength(hasher.outputByteSize)

                        else -> null
                    }

                    val passGen = KDFPassGen(
                        inputHasher = hasher,
                        passEncoder = encoder,
                        passwordLength = passLen
                    )

                    val result =
                        passGen.generate("valid_combo_test_input", utf8Codec)

                    assertNotNull(
                        result,
                        "Failed to generate password for ${hasher.key} and ${encoder.key}"
                    )

                    if (passLen != null) {
                        assertEquals(
                            passLen,
                            result.length,
                            "Length mismatch for ${hasher.key} and ${encoder.key}"
                        )
                    }
                }
            }
        }
}
