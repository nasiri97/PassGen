package ir.ornix.passgen.core.common.passwordgenerator.kdf

import ir.ornix.passgen.core.common.codec.BCryptBase64BinaryCodec
import ir.ornix.passgen.core.common.codec.Base64BinaryCodec
import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import ir.ornix.passgen.core.common.codec.Utf8TextCodec
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
    private val base64Codec = Base64BinaryCodec()
    private val bCryptCodec = BCryptBase64BinaryCodec()
    private val hexCodec = HexBinaryCodec(false)


    private suspend fun testCase1() = TestCase(
        input = "",
        argon2id = base64Codec.decode(
            "SIccW0Qtg/lQJyg+5S5HaSe5hzUqpzlpT0FZHH24rpPHsvua5MjBldLxXVCGYztmsJtLiU7D7QZWzN/mlNKkvQ"
        ),
        bcrypt = bCryptCodec.decode("qnIYintMZA9VQ3qWzqxEtvw4tXyO5Py"),
        sha512 = hexCodec.decode(
            "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"
        ),
        sha256 = hexCodec.decode("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
    )

    private suspend fun testCase2() = TestCase(
        input = " ",
        argon2id = base64Codec.decode(
            "1vjyDAtqCH+KQPxO0mhBR4zbIoF2919kBVnLuECm8M8Kq+3vc9oyLS6InLO2whmP0CCgTcNd83yKBqZTfNpzYw"
        ),
        bcrypt = bCryptCodec.decode("Qj5eZokAr/VoyT/B81gVcigGkXjv9e."),
        sha512 = hexCodec.decode(
            "f90ddd77e400dfe6a3fcf479b00b1ee29e7015c5bb8cd70f5f15b4886cc339275ff553fc8a053f8ddc7324f45168cffaf81f8c3ac93996f6536eef38e5e40768"
        ),
        sha256 = hexCodec.decode("36a9e7f1c95b82ffb99743e0c5c4ce95d83c9a430aac59f84ef3cbfab6145068")
    )

    private suspend fun testCase3() = TestCase(
        input = "\n\n  ",
        argon2id = base64Codec.decode(
            "jNwuBTBCAMjy3anuNFnZZqwhBB6BNpF+1BFFMX8xpVIzXVdPSOEp0/3nR00KuzAJivSpCGORKRlotWhqvnLVbw"
        ),
        bcrypt = bCryptCodec.decode("EmLve0j.AXy74JiSBFzVjpov6SCkJlK"),
        sha512 = hexCodec.decode(
            "6d317431699988eec1fbb52dadfe5d0bfb14d38398470401e1648c412244ee3c90bdbf627b21c37faf1e7ef00e5de1b69555362028a37b471da328c3fac59941"
        ),
        sha256 = hexCodec.decode("4308ef96ad0e86f35c795a177206056556333e814e65bfc9cd04bb164f8d61eb")
    )

    private suspend fun testCase4() = TestCase(
        input = "hello",
        argon2id = base64Codec.decode(
            "Jomso/aWbL3bXqb0Y+WuQOdMiveFYCBBC9FClw5pAihWocz0xFhc+Qns2PJZ3PhBp8doN1Eb9dyx36q1B50lBg"
        ),
        bcrypt = bCryptCodec.decode("aWoo2BxGM0sAUbeJTsGdDCs24koovhq"),
        sha512 = hexCodec.decode(
            "9b71d224bd62f3785d96d46ad3ea3d73319bfbc2890caadae2dff72519673ca72323c3d99ba5c11d7c7acc6e14b8c5da0c4663475c2e5c3adef46f73bcdec043"
        ),
        sha256 = hexCodec.decode("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824")
    )

    private suspend fun testCase5() = TestCase(
        input = "Hello World",
        argon2id = base64Codec.decode(
            "whbMNRoF5/S71AQakvFTqefPzAnppbAGVdGk3fKf9qselK/JGaY5fDawfqfVQ6hkTVWnMVoHzN7GiuwDz9a6/A"
        ),
        bcrypt = bCryptCodec.decode("OqWhN04gD3je48K05tPyPNL4w8snY0O"),
        sha512 = hexCodec.decode(
            "2c74fd17edafd80e8447b0d46741ee243b7eb74dd2149a0ab1b9246fb30382f27e853d8585719e0e67cbda0daa8f51671064615d645ae27acb15bfb1447f459b"
        ),
        sha256 = hexCodec.decode("a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e")
    )

    private suspend fun testCases() = listOf(
        testCase1(),
        testCase2(),
        testCase3(),
        testCase4(),
        testCase5()
    )


    @Test
    fun testIllegalPasswordLength() = runTest {
        InputHasher.allItems.forEach { inputHasher ->
            StringPassEncoder.allStringPassEncoders.forEach { passEncoder ->
                val maxLength =
                    (inputHasher.outputByteSize / passEncoder.binaryBlockSize) * passEncoder.encodedBlockSize

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
    fun testFullLength() = runTest {
        InputHasher.allItems.forEach { inputHasher ->
            StringPassEncoder.allStringPassEncoders.forEach { passEncoder ->
                val maxLength =
                    (inputHasher.outputByteSize / passEncoder.binaryBlockSize) * passEncoder.encodedBlockSize

                testCases().forEach { testCase ->

                    val expected = testCase.getDigest(
                        inputHasher = inputHasher,
                        length = null,
                        passEncoder = passEncoder
                    )

                    assertEquals(
                        expected,
                        KDFPassGen(
                            inputHasher = inputHasher,
                            passEncoder = passEncoder,
                            passwordLength = maxLength
                        ).generate(input = testCase.input, inputDecoder = utf8Codec)
                    )
                }
            }
        }
    }


    @Test
    fun testCustomLength() = runTest(timeout = 5.minutes) {
        InputHasher.allItems.forEach { inputHasher ->
            StringPassEncoder.allStringPassEncoders.forEach { passEncoder ->
                testCases().forEach { testCase ->
                    val maxLength =
                        (inputHasher.outputByteSize / passEncoder.binaryBlockSize) * passEncoder.encodedBlockSize

                    ((maxLength - 5)..maxLength).forEach { length ->
                        val expected = testCase.getDigest(
                            inputHasher = inputHasher,
                            length = length,
                            passEncoder = passEncoder
                        )

                        assertEquals(
                            expected,
                            KDFPassGen(
                                inputHasher = inputHasher,
                                passEncoder = passEncoder,
                                passwordLength = length
                            ).generate(input = testCase.input, inputDecoder = utf8Codec)
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
    fun testMaxEntropyByteSizeMatchesInputHasherOutputByteSize() {
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
    fun testStringPassEncoderWithNullPasswordLengthThrowsException() {
        InputHasher.allItems.forEach { inputHasher ->
            StringPassEncoder.allStringPassEncoders.forEach { passEncoder ->
                val exception = assertFailsWith<IllegalArgumentException> {
                    KDFPassGen(
                        inputHasher = inputHasher,
                        passEncoder = passEncoder,
                        passwordLength = null
                    )
                }
                assertEquals("Password must not be null for StringPassEncoder.", exception.message)
            }
        }
    }

    @Test
    fun testStringPassEncoderWithZeroPasswordLengthThrowsException() {
        InputHasher.allItems.forEach { inputHasher ->
            StringPassEncoder.allStringPassEncoders.forEach { passEncoder ->
                val exception = assertFailsWith<IllegalArgumentException> {
                    KDFPassGen(
                        inputHasher = inputHasher,
                        passEncoder = passEncoder,
                        passwordLength = 0
                    )
                }
                assertEquals("Password length must be greater than 0.", exception.message)
            }
        }
    }

    @Test
    fun testStringPassEncoderWithNegativePasswordLengthThrowsException() {
        InputHasher.allItems.forEach { inputHasher ->
            StringPassEncoder.allStringPassEncoders.forEach { passEncoder ->
                val exception = assertFailsWith<IllegalArgumentException> {
                    KDFPassGen(
                        inputHasher = inputHasher,
                        passEncoder = passEncoder,
                        passwordLength = -5
                    )
                }
                assertEquals("Password length must be greater than 0.", exception.message)
            }
        }
    }

    // -------------------------------------------------------------------------
    // SeedPassEncoder Validation Tests
    // -------------------------------------------------------------------------

    @Test
    fun testSeedPassEncoderWithNonNullPasswordLengthThrowsException() {
        InputHasher.allItems.forEach { inputHasher ->
            val validSeedEncoders = PassEncoder.getValidItems(inputHasher.outputByteSize)
                .filterIsInstance<SeedPassEncoder>()

            validSeedEncoders.forEach { seedEncoder ->
                val exception = assertFailsWith<IllegalArgumentException> {
                    KDFPassGen(
                        inputHasher = inputHasher,
                        passEncoder = seedEncoder,
                        passwordLength = 12
                    )
                }
                assertEquals("Password must be null for SeedPassEncoder.", exception.message)
            }
        }
    }

    @Test
    fun testSeedPassEncoderWithNullPasswordLengthInitializesSuccessfully() {
        val validSeedEncoders = PassEncoder.getValidItems(InputHasher.SHA512.outputByteSize)
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
    fun testSeedPassEncoderGenerationProducesDeterministicBip39Mnemonics() = runTest {
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

            // Generate with String + Decoder
            val resultString1 = passGen.generate("my_secret_seed_phrase_input", utf8Codec)
            val resultString2 = passGen.generate("my_secret_seed_phrase_input", utf8Codec)

            assertNotNull(resultString1)
            assertEquals(resultString1, resultString2, "Generation should be deterministic")

            val words = resultString1.trim().split(Regex("\\s+"))
            assertEquals(
                expectedWordCount,
                words.size,
                "Expected $expectedWordCount words for ${seedEncoder.key}"
            )

            // Generate with ByteArray directly
            val byteArrayInput = utf8Codec.decode("my_secret_seed_phrase_input")
            val resultBytes = passGen.generate(byteArrayInput)

            assertEquals(
                resultString1,
                resultBytes,
                "ByteArray generate should match String generate"
            )
        }
    }

    // -------------------------------------------------------------------------
    // Direct ByteArray Input & Decoder Parity Tests
    // -------------------------------------------------------------------------

    @Test
    fun testGenerateWithByteArrayInputMatchesGenerateWithDecoder() = runTest {
        val passGen = KDFPassGen(
            inputHasher = InputHasher.SHA256,
            passEncoder = StringPassEncoder.HexPassEncoder,
            passwordLength = 32
        )

        val inputStr = "Test Input String 123!"
        val inputBytes = utf8Codec.decode(inputStr)

        val passFromStr = passGen.generate(inputStr, utf8Codec)
        val passFromBytes = passGen.generate(inputBytes)

        assertNotNull(passFromStr)
        assertEquals(passFromStr, passFromBytes, "Result from String+Decoder must match ByteArray")
    }

    @Test
    fun testGenerateWithEmptyByteArrayInput() = runTest {
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
        assertEquals(pass1, pass2, "Empty byte array generation should be deterministic")
    }

    // -------------------------------------------------------------------------
    // Caching & Input Sensitivity Tests
    // -------------------------------------------------------------------------

    @Test
    fun testDeterminismAndInputSensitivity() = runTest {
        val passGen = KDFPassGen(
            inputHasher = InputHasher.SHA256,
            passEncoder = StringPassEncoder.HexPassEncoder,
            passwordLength = 32
        )

        val input1 = "secret_password_1".encodeToByteArray()
        val input2 = "secret_password_2".encodeToByteArray()

        val result1a = passGen.generate(input1)
        val result1b = passGen.generate(input1) // Cached / repeated
        val result2 = passGen.generate(input2)  // Different input
        val result1c = passGen.generate(input1) // Switched back

        assertNotNull(result1a)
        assertNotNull(result1b)
        assertNotNull(result2)
        assertNotNull(result1c)

        assertEquals(
            result1a,
            result1b,
            "Repeated calls with same input should return identical password"
        )
        assertNotEquals(result1a, result2, "Different inputs must produce different passwords")
        assertEquals(
            result1a,
            result1c,
            "Switching back to original input should return original password"
        )
    }

    // -------------------------------------------------------------------------
    // Valid Combinations Tests
    // -------------------------------------------------------------------------

    @Test
    fun testAllValidHasherAndEncoderCombinations() = runTest {
        for (hasher in InputHasher.allItems) {
            val validEncoders = PassEncoder.getValidItems(hasher)

            for (encoder in validEncoders) {
                val passLen = when (encoder) {
                    is StringPassEncoder -> encoder.getTokenLength(hasher.outputByteSize)
                    else -> null
                }

                val passGen = KDFPassGen(
                    inputHasher = hasher,
                    passEncoder = encoder,
                    passwordLength = passLen
                )

                val result = passGen.generate("valid_combo_test_input", utf8Codec)

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


    private class TestCase(
        val input: String,
        val argon2id: ByteArray,
        val bcrypt: ByteArray,
        val sha512: ByteArray,
        val sha256: ByteArray
    ) {

        /**
         * @param length Set null for full-length
         */
        suspend fun getDigest(
            inputHasher: InputHasher,
            length: Int?,
            passEncoder: StringPassEncoder
        ): String {
            val binaryBlockSize: Int
            val encodedBlockSize: Int

            val bytes = when (inputHasher) {
                is InputHasher.ARGON2ID -> argon2id
                is InputHasher.BCrypt -> bcrypt
                is InputHasher.SHA512 -> sha512
                is InputHasher.SHA256 -> sha256
            }

            when (passEncoder) {
                is StringPassEncoder.HexPassEncoder -> {
                    binaryBlockSize = 1
                    encodedBlockSize = 2
                }

                is StringPassEncoder.Base64PassEncoder -> {
                    binaryBlockSize = 3
                    encodedBlockSize = 4
                }

                is StringPassEncoder.Z85PassEncoder -> {
                    binaryBlockSize = 4
                    encodedBlockSize = 5
                }
            }

            val maxSize = bytes.size - (bytes.size % binaryBlockSize)
            val validBytes = bytes.copyOfRange(0, maxSize)
            val encoded = passEncoder.encodeAllBytes(validBytes)

            check(encoded.length == (maxSize / binaryBlockSize) * encodedBlockSize) {
                "Encoded size must be a multiple of encodedBlockSize!"
            }

            return if (length == null) encoded
            else encoded.substring(0, length)
        }
    }

}
