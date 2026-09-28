package ir.ornix.passgen.core.domain.passgen

import ir.ornix.passgen.core.common.codec.BCryptBase64BinaryCodec
import ir.ornix.passgen.core.common.codec.Base64BinaryCodec
import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import ir.ornix.passgen.core.common.codec.Z85BinaryCodec
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.domain.FakeHmacSigner
import ir.ornix.passgen.core.domain.FakePassGenConfigRepository
import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import ir.ornix.passgen.core.domain.passgenconfig.model.PreprocessConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
class GenerateKDFPassUseCaseTest {

    private lateinit var configRepo: FakePassGenConfigRepository
    private lateinit var hmacSigner: FakeHmacSigner
    private lateinit var useCase: GenerateKDFPassUseCase

    private val masterKey = "12345678 12345678 12345678 12345678".encodeToByteArray()

    private val rawPreprocessConfig = PreprocessConfig(
        trimLeadingAndTrailingSpaces = false,
        collapseMultipleSpaces = false,
        convertToLowercase = false,
    )

    companion object {
        private val TIMEOUT = 3.minutes

        private val hexCodec = HexBinaryCodec(false)
        private val base64Codec = Base64BinaryCodec()
        private val bCryptBase64Codec = BCryptBase64BinaryCodec()
        private val z85Codec = Z85BinaryCodec()
    }

    @BeforeTest
    fun setup() {
        configRepo = FakePassGenConfigRepository()
        hmacSigner = FakeHmacSigner()
        useCase = GenerateKDFPassUseCase(configRepo, hmacSigner)
    }

    @Test
    fun `known vector test with empty input`() = runTest(timeout = TIMEOUT) {
        verifyKnownVectors(
            input = "",
            expectedSha256Hex = "76f980c25c9e2e9ca2b8f2bb33200e4b0673bb892ed337491401ef163f544dca",
            expectedSha512Hex = "029767bcc783104673aeeda67b4a439733db9223594f41de69b493b0dc6f2341f3332e7c3c2cb64224caafa92a375f2084505d7b4b7558bcb941488abac67e95",
            expectedBcryptBase64 = $$"$2b$12$btk.ujwcJnwgsNI5Kw.MQu6RirA9yZd9Qc5CtxBY./HtqKGP8H.iK",
            expectedArgon2Base64 = $$"$argon2id$v=19$m=131072,t=4,p=1$dvmAwlyeLpyiuPK7MyAOSw$dxdr2FAxNuvr/0Yfe3SXzoqUg1HFPOZtAgKbpEWD9b9Mk6RfOxSNcOBuGvGcHRq+QMAS1/irS5ZVfKgrYRVsMg",
        )
    }

    @Test
    fun `known vector test with single space input`() = runTest(timeout = TIMEOUT) {
        verifyKnownVectors(
            input = " ",
            expectedSha256Hex = "d770f5f183b61182a1bb9bff31f0c5f8d93abe074487ac332f916f1c6cf0ae34",
            expectedSha512Hex = "97edf451f881fbf88c8fa1007198d047bcd87d5aee45dad280302610da62e5f85db95b4c194d3fb04c7a4d712bbfa79c6cfc1a80ea7cb9af68ebfbbe77455f9a",
            expectedBcryptBase64 = $$"$2b$12$z1Bz6WM0CWIfs3t9KdBD8.7xNkZrKT5xCfcTHANnz7i7V35LsO9/C",
            expectedArgon2Base64 = $$"$argon2id$v=19$m=131072,t=4,p=1$13D18YO2EYKhu5v/MfDF+A$69KMiSjp6EhNGgaxcnOOgBjuISQpxdMjNjYt7lHKV3X/wZkb5N3mQQ3+NN0+2yIEEKim054TQREdQobBRF2qJA",
        )
    }

    @Test
    fun `known vector test with hello input`() = runTest(timeout = TIMEOUT) {
        verifyKnownVectors(
            input = "Hello",
            expectedSha256Hex = "6d17c19b39f98ade1b1cc17be203684863a31a9ffdf2d8d6aaac7fe554a83d08",
            expectedSha512Hex = "35ffa3e2ca39d6371e03a6e1fb409b51e619405aff5b174f0d02974592f2f12fdf9bbf9053dce330b2925a75f3726c4afceeea7ab7225386b54ea7af1e856ab6",
            expectedBcryptBase64 = $$"$2b$12$ZPd/kxl3gr2ZFKD52eLmQ.J7ugRM58kK.1gZKzs33A52jKIM/7Z32",
            expectedArgon2Base64 = $$"$argon2id$v=19$m=131072,t=4,p=1$bRfBmzn5it4bHMF74gNoSA$+Euaz/t2ET7kqOnNOTFtfGCEroz47Jp7lKvnjmONFv6yB7SQVT85trjOVHQorwzrkyJ3uPsUpJOTvd2wlDVCcA",
        )
    }

    @Test
    fun `known vector test with hello world input`() = runTest(timeout = TIMEOUT) {
        verifyKnownVectors(
            input = "Hello World",
            expectedSha256Hex = "84d511f33a785ca1d35f44dd8d2915a0c266fb2c3b5309e602ce5bd62217f445",
            expectedSha512Hex = "a9ef7a4884d929a2d8921d7e2270b337fa35bb0acd7a57e81ad541d1fd08ee2497b0ce1e9276487b9d948dfd0fbfb62bcb212c4f422626cc202970a42448358f",
            expectedBcryptBase64 = $$"$2b$12$fLSP6xn2VIFRVyRbhQiTm.vGAmlg.KJA07fOreMXAxHlQNWoFWOPq",
            expectedArgon2Base64 = $$"$argon2id$v=19$m=131072,t=4,p=1$hNUR8zp4XKHTX0TdjSkVoA$Uvlp94n6XLcr90JN6d9X3Zka7PDAP9WWp7/7vj4dZbjUwlKnQ5IMaqd3SXE4gLP5ZOLGul6bmycD3ayor1h47w",
        )
    }

    @Test
    fun `invoke returns password when key is registered`() = runTest(timeout = TIMEOUT) {
        val config = KDFPassGenConfig(
            id = 100,
            name = "Test Config",
            preprocessConfig = rawPreprocessConfig,
            inputHasher = InputHasher.SHA256,
            passEncoder = StringPassEncoder.HexPassEncoder,
            passwordLength = 32,
        )
        configRepo.add(config)
        hmacSigner.registerKey("100", masterKey)

        val result = useCase(config, "secret_input")

        assertNotNull(result)
        assertEquals(32, result.value.length)
    }

    @Test
    fun `invoke removes config and returns null when signing key is missing`() =
        runTest(timeout = TIMEOUT) {
            val config = KDFPassGenConfig(
                id = 200,
                name = "Missing Key Config",
                preprocessConfig = rawPreprocessConfig,
                inputHasher = InputHasher.SHA256,
                passEncoder = StringPassEncoder.HexPassEncoder,
                passwordLength = 16,
            )
            configRepo.add(config)
            assertEquals(1, configRepo.getAll().first().size)

            val result = useCase(config, "secret_input")

            assertNull(result)
            assertTrue(configRepo.getAll().first().isEmpty())
        }

    @Test
    fun `invoke applies preprocess config to input before generating password`() =
        runTest(timeout = TIMEOUT) {
            val preprocessConfig = PreprocessConfig(
                trimLeadingAndTrailingSpaces = true,
                collapseMultipleSpaces = true,
                convertToLowercase = true,
            )
            val config = KDFPassGenConfig(
                id = 300,
                name = "Preprocess Config",
                preprocessConfig = preprocessConfig,
                inputHasher = InputHasher.SHA256,
                passEncoder = StringPassEncoder.HexPassEncoder,
                passwordLength = 32,
            )
            configRepo.add(config)
            hmacSigner.registerKey("300", masterKey)

            val result1 = useCase(config, "  HELLO    WORLD  ")
            val result2 = useCase(config, "hello world")

            assertNotNull(result1)
            assertNotNull(result2)
            assertEquals(result2.value, result1.value)
        }

    @Test
    fun `invoke supports SeedPassEncoder with null password length`() = runTest(timeout = TIMEOUT) {
        val config = KDFPassGenConfig(
            id = 400,
            name = "Bip39 Config",
            preprocessConfig = rawPreprocessConfig,
            inputHasher = InputHasher.SHA512,
            passEncoder = SeedPassEncoder.Bip39L12PassEncoder,
            passwordLength = null,
        )
        configRepo.add(config)
        hmacSigner.registerKey("400", masterKey)

        val result = useCase(config, "bip39_input")

        assertNotNull(result)
        val words = result.value.trim().split("\\s+".toRegex())
        assertEquals(12, words.size)
    }

    @Test
    fun `invoke supports Z85PassEncoder`() = runTest(timeout = TIMEOUT) {
        val config = KDFPassGenConfig(
            id = 500,
            name = "Z85 Config",
            preprocessConfig = rawPreprocessConfig,
            inputHasher = InputHasher.SHA256,
            passEncoder = StringPassEncoder.Z85PassEncoder,
            passwordLength = 40,
        )
        configRepo.add(config)
        hmacSigner.registerKey("500", masterKey)

        val result = useCase(config, "z85_input")

        assertNotNull(result)
        assertEquals(40, result.value.length)
    }

    private suspend fun verifyKnownVectors(
        input: String,
        expectedSha256Hex: String,
        expectedSha512Hex: String,
        expectedBcryptBase64: String,
        expectedArgon2Base64: String,
    ) {
        val sha256Config = KDFPassGenConfig(
            id = 1,
            name = "SHA256 Config",
            preprocessConfig = rawPreprocessConfig,
            inputHasher = InputHasher.SHA256,
            passEncoder = StringPassEncoder.HexPassEncoder,
            passwordLength = 64,
        )

        val sha512Config = KDFPassGenConfig(
            id = 2,
            name = "SHA512 Config",
            preprocessConfig = rawPreprocessConfig,
            inputHasher = InputHasher.SHA512,
            passEncoder = StringPassEncoder.Base64PassEncoder,
            passwordLength = 24,
        )

        val bcryptConfig = KDFPassGenConfig(
            id = 3,
            name = "BCrypt Config",
            preprocessConfig = rawPreprocessConfig,
            inputHasher = InputHasher.BCrypt,
            passEncoder = StringPassEncoder.Z85PassEncoder,
            passwordLength = 25,
        )

        val argon2Config = KDFPassGenConfig(
            id = 4,
            name = "Argon2 Config",
            preprocessConfig = rawPreprocessConfig,
            inputHasher = InputHasher.ARGON2ID,
            passEncoder = StringPassEncoder.Z85PassEncoder,
            passwordLength = 80,
        )

        hmacSigner.registerKey("1", masterKey)
        hmacSigner.registerKey("2", masterKey)
        hmacSigner.registerKey("3", masterKey)
        hmacSigner.registerKey("4", masterKey)

        val sha256Result = useCase(sha256Config, input)
        val sha512Result = useCase(sha512Config, input)
        val bcryptResult = useCase(bcryptConfig, input)
        val argon2Result = useCase(argon2Config, input)

        assertNotNull(sha256Result)
        assertNotNull(sha512Result)
        assertNotNull(bcryptResult)
        assertNotNull(argon2Result)

        // Sha256
        assertEquals(expectedSha256Hex, sha256Result.value, "SHA256 failed for input '$input'")

        // Sha512
        val sExpected = base64Codec.encode(hexCodec.decode(expectedSha512Hex)).take(24)
        assertEquals(sExpected, sha512Result.value, "SHA512 failed for input '$input'")

        // BCrypt
        assertEquals(
            z85Codec.encode(
                bCryptBase64Codec.decode(expectedBcryptBase64.substring(29)).copyOfRange(0, 20)
            ).take(25),
            bcryptResult.value,
            "BCrypt failed for input '$input'"
        )

        // Argon 2ID
        assertEquals(
            z85Codec.encode(
                base64Codec.decode(expectedArgon2Base64.substring(55))
            ).take(80),
            argon2Result.value,
            "Argon2 failed for input '$input'"
        )
    }
}
