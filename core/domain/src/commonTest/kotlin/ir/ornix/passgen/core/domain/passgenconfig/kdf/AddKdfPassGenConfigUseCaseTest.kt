package ir.ornix.passgen.core.domain.passgenconfig.kdf

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.domain.FakeHmacSigner
import ir.ornix.passgen.core.domain.FakeKdfPassGenConfigRepository
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.KdfPassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AddKdfPassGenConfigUseCaseTest {

    private lateinit var repository: FakeKdfPassGenConfigRepository
    private lateinit var hmacSigner: FakeHmacSigner
    private lateinit var useCase: AddKdfPassGenConfigUseCase

    private val masterKey1 = "master_key_1234567890_1234567890".toCharArray()
    private val masterKey2 = "another_secret_master_key_98765".toCharArray()

    private val testConfig1 = KdfPassGenConfig(
        id = 10,
        name = "Test Config 10",
        masterKeyDigestId = "master_key_10",
        preprocessConfig = PreprocessConfig(
            trimLeadingAndTrailingSpaces = true,
            collapseMultipleSpaces = false,
            convertToLowercase = false
        ),
        inputHasher = InputHasher.SHA256,
        passEncoder = StringPassEncoder.HexPassEncoder,
        passwordLength = 32
    )

    private val testConfig2 = KdfPassGenConfig(
        id = 20,
        name = "Test Config 20",
        masterKeyDigestId = "master_key_20",
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
        repository = FakeKdfPassGenConfigRepository()
        hmacSigner = FakeHmacSigner()
        useCase = AddKdfPassGenConfigUseCase(repository, hmacSigner)
    }

    @Test
    fun `invoke should add config to repository and register key in hmacSigner`() = runTest {
        useCase(testConfig1, masterKey1)

        // Verify repository stores the config
        val configs = repository.getAllConfigs().first()
        assertEquals(1, configs.size)
        assertEquals(testConfig1, configs.first())

        // Verify hmacSigner has key registered under stringified config ID
        val hasKey = hmacSigner.hasMasterKeyDigest(testConfig1.masterKeyDigestId)
        assertTrue(
            hasKey,
            "HmacSigner should have master key digest registered for config ID '${testConfig1.id}'"
        )

        // Verify hmacSigner can sign using the registered key
        val signature = hmacSigner.sign(testConfig1.masterKeyDigestId, "input_data")
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

            val configs = repository.getAllConfigs().first()
            assertEquals(2, configs.size)
            assertEquals(listOf(testConfig1, testConfig2), configs)

            assertTrue(hmacSigner.hasMasterKeyDigest(testConfig1.masterKeyDigestId))
            assertTrue(hmacSigner.hasMasterKeyDigest(testConfig2.masterKeyDigestId))
        }

    @Test
    fun `invoke should work with empty raw key byte array`() = runTest {
        val emptyKey = CharArray(0)
        useCase(testConfig1, emptyKey)

        val configs = repository.getAllConfigs().first()
        assertEquals(1, configs.size)
        assertTrue(hmacSigner.hasMasterKeyDigest(testConfig1.masterKeyDigestId))
    }

    @Test
    fun `invoke should propagate exception when repository add fails and not register key in signer`() =
        runTest {
            val failingRepo = object : KdfPassGenConfigRepository {
                override suspend fun getConfigById(configId: Long): KdfPassGenConfig? =
                    repository.getConfigById(configId)

                override suspend fun addConfig(config: KdfPassGenConfig): Long {
                    throw IllegalStateException("Database write failed")
                }

                override suspend fun removeConfigById(configId: Long) {}
                override fun getAllConfigs() = repository.getAllConfigs()
                override suspend fun deleteAllConfigs() {}

                override suspend fun doesMasterKeyDigestIdExist(masterKeyDigestId: String) =
                    repository.doesMasterKeyDigestIdExist(masterKeyDigestId)

                override suspend fun getAllMasterKeyDigestIds() =
                    repository.getAllMasterKeyDigestIds()
            }

            val failingUseCase = AddKdfPassGenConfigUseCase(failingRepo, hmacSigner)

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
            override suspend fun registerKey(mkdId: String, rawMasterKey: ByteArray): Boolean {
                throw IllegalArgumentException("Key registration failed")
            }

            override suspend fun sign(mkdId: String, input: String): ByteArray = ByteArray(0)
            override suspend fun hasMasterKeyDigest(mkdId: String): Boolean = false
            override suspend fun deleteMasterKeyDigest(mkdId: String) {}
        }

        val failingUseCase = AddKdfPassGenConfigUseCase(repository, failingSigner)

        val exception = assertFailsWith<IllegalArgumentException> {
            failingUseCase(testConfig1, masterKey1)
        }

        assertEquals("Key registration failed", exception.message)
        assertEquals(0, repository.getAllConfigs().first().size)
    }
}
