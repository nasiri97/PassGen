package ir.ornix.passgen.core.domain.passgenconfig

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.domain.FakeHmacSigner
import ir.ornix.passgen.core.domain.FakePassGenConfigRepository
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import ir.ornix.passgen.core.domain.passgenconfig.model.PreprocessConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AddPassGenConfigUseCaseTest {

    private lateinit var repository: FakePassGenConfigRepository
    private lateinit var hmacSigner: FakeHmacSigner
    private lateinit var useCase: AddPassGenConfigUseCase

    private val masterKey1 = "master_key_1234567890_1234567890".encodeToByteArray()
    private val masterKey2 = "another_secret_master_key_98765".encodeToByteArray()

    private val testConfig1 = KDFPassGenConfig(
        id = 10,
        name = "Test Config 10",
        preprocessConfig = PreprocessConfig(
            trimLeadingAndTrailingSpaces = true,
            collapseMultipleSpaces = false,
            convertToLowercase = false
        ),
        inputHasher = InputHasher.SHA256,
        passEncoder = StringPassEncoder.HexPassEncoder,
        passwordLength = 32
    )

    private val testConfig2 = KDFPassGenConfig(
        id = 20,
        name = "Test Config 20",
        preprocessConfig = PreprocessConfig(
            trimLeadingAndTrailingSpaces = false,
            collapseMultipleSpaces = true,
            convertToLowercase = true
        ),
        inputHasher = InputHasher.SHA512,
        passEncoder = StringPassEncoder.Base64PassEncoder,
        passwordLength = 64
    )

    @BeforeTest
    fun setup() {
        repository = FakePassGenConfigRepository()
        hmacSigner = FakeHmacSigner()
        useCase = AddPassGenConfigUseCase(repository, hmacSigner)
    }

    @Test
    fun `invoke should add config to repository and register key in hmacSigner`() = runTest {
        useCase(testConfig1, masterKey1)

        // Verify repository stores the config
        val configs = repository.getAll().first()
        assertEquals(1, configs.size)
        assertEquals(testConfig1, configs.first())

        // Verify hmacSigner has key registered under stringified config ID
        val hasKey = hmacSigner.hasMasterKeyDigest("${testConfig1.id}")
        assertTrue(
            hasKey,
            "HmacSigner should have master key digest registered for config ID '${testConfig1.id}'"
        )

        // Verify hmacSigner can sign using the registered key
        val signature = hmacSigner.sign("${testConfig1.id}", "input_data")
        assertNotNull(signature)
        assertTrue(
            signature.isNotEmpty(),
            "Signature produced by registered key should not be empty"
        )
    }

    @Test
    fun `invoke should handle multiple config additions with distinct IDs and raw keys`() =
        runTest {
            useCase(testConfig1, masterKey1)
            useCase(testConfig2, masterKey2)

            val configs = repository.getAll().first()
            assertEquals(2, configs.size)
            assertEquals(listOf(testConfig1, testConfig2), configs)

            assertTrue(hmacSigner.hasMasterKeyDigest("${testConfig1.id}"))
            assertTrue(hmacSigner.hasMasterKeyDigest("${testConfig2.id}"))
        }

    @Test
    fun `invoke should work with empty raw key byte array`() = runTest {
        val emptyKey = ByteArray(0)
        useCase(testConfig1, emptyKey)

        val configs = repository.getAll().first()
        assertEquals(1, configs.size)
        assertTrue(hmacSigner.hasMasterKeyDigest("${testConfig1.id}"))
    }

    @Test
    fun `invoke should propagate exception when repository add fails and not register key in signer`() =
        runTest {
            val failingRepo = object : PassGenConfigRepository {
                override suspend fun add(config: KDFPassGenConfig): Int {
                    throw IllegalStateException("Database write failed")
                }

                override suspend fun removeById(configId: Int) {}
                override fun getAll(): Flow<List<KDFPassGenConfig>> = repository.getAll()
            }

            val failingUseCase = AddPassGenConfigUseCase(failingRepo, hmacSigner)

            val exception = assertFailsWith<IllegalStateException> {
                failingUseCase(testConfig1, masterKey1)
            }

            assertEquals("Database write failed", exception.message)
            assertTrue(
                !hmacSigner.hasMasterKeyDigest("${testConfig1.id}"),
                "Signer should not have key registered if repository add failed"
            )
        }

    @Test
    fun `invoke should propagate exception when hmacSigner registerKey fails`() = runTest {
        val failingSigner = object : HmacSigner {
            override suspend fun registerKey(mkdId: String, rawMasterKey: ByteArray) {
                throw IllegalArgumentException("Key registration failed")
            }

            override suspend fun sign(mkdId: String, input: String): ByteArray = ByteArray(0)
            override suspend fun hasMasterKeyDigest(mkdId: String): Boolean = false
            override suspend fun deleteMasterKeyDigest(mkdId: String) {}
        }

        val failingUseCase = AddPassGenConfigUseCase(repository, failingSigner)

        val exception = assertFailsWith<IllegalArgumentException> {
            failingUseCase(testConfig1, masterKey1)
        }

        assertEquals("Key registration failed", exception.message)
        // Note: repo add succeeded first
        assertEquals(1, repository.getAll().first().size)
    }
}
