package ir.ornix.passgen.core.domain.passgen

import ir.ornix.passgen.core.common.codec.BCryptBase64BinaryCodec
import ir.ornix.passgen.core.common.codec.Base64BinaryCodec
import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import ir.ornix.passgen.core.common.codec.Z85BinaryCodec
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.domain.FakeHmacSigner
import ir.ornix.passgen.core.domain.FakePassGenConfigRepository
import ir.ornix.passgen.core.domain.passgenconfig.AddPassGenConfigUseCase
import ir.ornix.passgen.core.domain.passgenconfig.GetAllPassGenConfigsUseCase
import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import ir.ornix.passgen.core.domain.passgenconfig.model.PreprocessConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
class GenerateKDFPassUseCaseTest {

    private lateinit var useCase: GenerateKDFPassUseCase
    private lateinit var repository: FakePassGenConfigRepository
    private lateinit var hmacSigner: FakeHmacSigner
    private lateinit var getAllPassGenConfigs: GetAllPassGenConfigsUseCase
    private lateinit var addPassGenConfigUseCase: AddPassGenConfigUseCase

    companion object {
        private val hexCodec = HexBinaryCodec(false)
        private val base64Codec = Base64BinaryCodec()
        private val bCryptBase64Codec = BCryptBase64BinaryCodec()
        private val z85Codec = Z85BinaryCodec()

        private val TIMEOUT = 3.minutes

        private const val MASTER_KEY = "12345678 12345678 12345678 12345678"
    }

    @BeforeTest
    fun setup() {
        repository = FakePassGenConfigRepository()
        hmacSigner = FakeHmacSigner()
        getAllPassGenConfigs = GetAllPassGenConfigsUseCase(repository)
        useCase = GenerateKDFPassUseCase(getAllPassGenConfigs, hmacSigner)
        addPassGenConfigUseCase = AddPassGenConfigUseCase(repository, hmacSigner)
    }

    @Test
    fun `invoke should return wrappers for all configs`() = runTest {
        val config1 = createConfig(id = 1, name = "Config 1")
        val config2 = createConfig(id = 2, name = "Config 2")
        repository.add(config1)
        repository.add(config2)

        val wrappers = useCase(MutableStateFlow("")).first()

        assertEquals(2, wrappers.size)
        assertEquals(config1.id, wrappers[0].passGenConfig.id)
        assertEquals(config2.id, wrappers[1].passGenConfig.id)
    }

    @Test
    fun `useCase should recycle existing wrappers when configs change`() = runTest {
        val config1 = createConfig(id = 1, name = "Config 1")
        repository.add(config1)

        val input = MutableStateFlow("")
        val wrappers1 = useCase(input).first()
        val wrapper1 = wrappers1[0]

        repository.add(createConfig(id = 2, name = "Config 2"))
        val wrappers2 = useCase(input).first()

        assertEquals(2, wrappers2.size)
        val sameWrapper1 = wrappers2.find { it.passGenConfig.id == 1 }
        assertNotNull(sameWrapper1)
        assertSame(wrapper1, sameWrapper1)
    }

    @Test
    fun `wrappers should generate different passwords for different inputs`() =
        runTest(timeout = TIMEOUT) {
            val configId = 1
            repository.add(createConfig(id = configId, name = "Main"))
            hmacSigner.registerKey(configId.toString(), "secret".encodeToByteArray())

            val inputFlow = MutableStateFlow("input1")
            val wrapper = useCase(inputFlow).first().first()

            val pass1 = wrapper.password.filterNotNull().first()
            inputFlow.value = "input2"
            val pass2 = wrapper.password.filterNotNull().first { it.value != pass1.value }

            assertNotNull(pass1)
            assertNotNull(pass2)
            assertNotSame(pass1.value, pass2.value)
        }

    @Test
    fun `known vector test with empty input`() = runTest(timeout = TIMEOUT) {
        verifyKnownVectors(
            input = "",
            expectedSha256Hex = "76f980c25c9e2e9ca2b8f2bb33200e4b0673bb892ed337491401ef163f544dca",
            expectedSha512Hex = "029767bcc783104673aeeda67b4a439733db9223594f41de69b493b0dc6f2341f3332e7c3c2cb64224caafa92a375f2084505d7b4b7558bcb941488abac67e95",
            expectedBcryptBase64 = $$"$2b$12$btk.ujwcJnwgsNI5Kw.MQu6RirA9yZd9Qc5CtxBY./HtqKGP8H.iK",
            expectedArgon2Base64 = $$"$argon2id$v=19$m=131072,t=4,p=1$dvmAwlyeLpyiuPK7MyAOSw$dxdr2FAxNuvr/0Yfe3SXzoqUg1HFPOZtAgKbpEWD9b9Mk6RfOxSNcOBuGvGcHRq+QMAS1/irS5ZVfKgrYRVsMg"
        )
    }

    @Test
    fun `known vector test with single space input`() = runTest(timeout = TIMEOUT) {
        verifyKnownVectors(
            input = " ",
            expectedSha256Hex = "d770f5f183b61182a1bb9bff31f0c5f8d93abe074487ac332f916f1c6cf0ae34",
            expectedSha512Hex = "97edf451f881fbf88c8fa1007198d047bcd87d5aee45dad280302610da62e5f85db95b4c194d3fb04c7a4d712bbfa79c6cfc1a80ea7cb9af68ebfbbe77455f9a",
            expectedBcryptBase64 = $$"$2b$12$z1Bz6WM0CWIfs3t9KdBD8.7xNkZrKT5xCfcTHANnz7i7V35LsO9/C",
            expectedArgon2Base64 = $$"$argon2id$v=19$m=131072,t=4,p=1$13D18YO2EYKhu5v/MfDF+A$69KMiSjp6EhNGgaxcnOOgBjuISQpxdMjNjYt7lHKV3X/wZkb5N3mQQ3+NN0+2yIEEKim054TQREdQobBRF2qJA"
        )
    }

    @Test
    fun `known vector test with hello input`() = runTest(timeout = TIMEOUT) {
        verifyKnownVectors(
            input = "Hello",
            expectedSha256Hex = "6d17c19b39f98ade1b1cc17be203684863a31a9ffdf2d8d6aaac7fe554a83d08",
            expectedSha512Hex = "35ffa3e2ca39d6371e03a6e1fb409b51e619405aff5b174f0d02974592f2f12fdf9bbf9053dce330b2925a75f3726c4afceeea7ab7225386b54ea7af1e856ab6",
            expectedBcryptBase64 = $$"$2b$12$ZPd/kxl3gr2ZFKD52eLmQ.J7ugRM58kK.1gZKzs33A52jKIM/7Z32",
            expectedArgon2Base64 = $$"$argon2id$v=19$m=131072,t=4,p=1$bRfBmzn5it4bHMF74gNoSA$+Euaz/t2ET7kqOnNOTFtfGCEroz47Jp7lKvnjmONFv6yB7SQVT85trjOVHQorwzrkyJ3uPsUpJOTvd2wlDVCcA"
        )
    }

    @Test
    fun `known vector test with hello world input`() = runTest(timeout = TIMEOUT) {
        verifyKnownVectors(
            input = "Hello World",
            expectedSha256Hex = "84d511f33a785ca1d35f44dd8d2915a0c266fb2c3b5309e602ce5bd62217f445",
            expectedSha512Hex = "a9ef7a4884d929a2d8921d7e2270b337fa35bb0acd7a57e81ad541d1fd08ee2497b0ce1e9276487b9d948dfd0fbfb62bcb212c4f422626cc202970a42448358f",
            expectedBcryptBase64 = $$"$2b$12$fLSP6xn2VIFRVyRbhQiTm.vGAmlg.KJA07fOreMXAxHlQNWoFWOPq",
            expectedArgon2Base64 = $$"$argon2id$v=19$m=131072,t=4,p=1$hNUR8zp4XKHTX0TdjSkVoA$Uvlp94n6XLcr90JN6d9X3Zka7PDAP9WWp7/7vj4dZbjUwlKnQ5IMaqd3SXE4gLP5ZOLGul6bmycD3ayor1h47w"
        )
    }

    private suspend fun verifyKnownVectors(
        input: String,
        expectedSha256Hex: String,
        expectedSha512Hex: String,
        expectedBcryptBase64: String,
        expectedArgon2Base64: String
    ) {
        val configs = listOf(
            createConfig(1, "SHA256", InputHasher.SHA256, StringPassEncoder.HexPassEncoder, 24),
            createConfig(2, "SHA512", InputHasher.SHA512, StringPassEncoder.Base64PassEncoder, 24),
            createConfig(3, "BCrypt", InputHasher.BCrypt, StringPassEncoder.Z85PassEncoder, 24),
            createConfig(4, "Argon2", InputHasher.ARGON2ID, StringPassEncoder.Z85PassEncoder, 24)
        )

        configs.forEach { addPassGenConfigUseCase(it, MASTER_KEY.encodeToByteArray()) }

        val wrappers = useCase(MutableStateFlow(input)).first()

        wrappers.forEach { wrapper ->
            val actual = wrapper.password.filterNotNull().first().value
            val length = wrapper.passGenConfig.passwordLength

            when (wrapper.passGenConfig.name) {
                "SHA256" -> {
                    val expected = hexCodec.encode(hexCodec.decode(expectedSha256Hex)).take(length)
                    assertEquals(expected, actual, "SHA256 mismatch for input '$input'")
                }

                "SHA512" -> {
                    val expected =
                        base64Codec.encode(hexCodec.decode(expectedSha512Hex)).take(length)
                    assertEquals(expected, actual, "SHA512 mismatch for input '$input'")
                }

                "BCrypt" -> {
                    val rawHash = bCryptBase64Codec.decode(expectedBcryptBase64.substring(29))
                    val expected = z85Codec.encode(rawHash.copyOfRange(0, 20)).take(length)
                    assertEquals(expected, actual, "BCrypt mismatch for input '$input'")
                }

                "Argon2" -> {
                    val rawHash = base64Codec.decode(expectedArgon2Base64.substring(55))
                    val expected = z85Codec.encode(rawHash).take(length)
                    assertEquals(expected, actual, "Argon2 mismatch for input '$input'")
                }
            }
        }
    }

    private fun createConfig(
        id: Int,
        name: String,
        inputHasher: InputHasher = InputHasher.SHA256,
        passEncoder: StringPassEncoder = StringPassEncoder.HexPassEncoder,
        length: Int = 16
    ) = KDFPassGenConfig(
        id = id,
        name = name,
        preprocessConfig = PreprocessConfig(
            trimLeadingAndTrailingSpaces = false,
            collapseMultipleSpaces = false,
            convertToLowercase = false
        ),
        inputHasher = inputHasher,
        passEncoder = passEncoder,
        passwordLength = length
    )
}
