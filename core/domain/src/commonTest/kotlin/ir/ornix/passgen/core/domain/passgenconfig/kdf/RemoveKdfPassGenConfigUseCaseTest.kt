package ir.ornix.passgen.core.domain.passgenconfig.kdf

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.domain.FakeHmacSigner
import ir.ornix.passgen.core.domain.FakeKdfPassGenConfigRepository
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.KdfPassGenConfigRepository
import ir.ornix.passgen.core.domain.SigningKeyNotFoundException
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RemoveKdfPassGenConfigUseCaseTest {

    private lateinit var repository: FakeKdfPassGenConfigRepository
    private lateinit var hmacSigner: FakeHmacSigner
    private lateinit var useCase: RemoveKdfPassGenConfigUseCase

    private val masterKey1 = "master_key_1".encodeToByteArray()
    private val masterKey2 = "master_key_2".encodeToByteArray()

    private val testConfig1 = KdfPassGenConfig(
        id = 100,
        name = "Config 100",
        masterKeyDigestId = "master_key_100",
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
        id = 200,
        name = "Config 200",
        masterKeyDigestId = "master_key_200",
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
        useCase = RemoveKdfPassGenConfigUseCase(repository, hmacSigner)
    }

    @Test
    fun `invoke should remove existing config from repository and key digest from hmacSigner`() =
        runTest {
            repository.addConfig(testConfig1)
            hmacSigner.registerKey(testConfig1.masterKeyDigestId, masterKey1)

            // Verify initial state
            assertEquals(1, repository.getAllConfigs().first().size)
            assertTrue(hmacSigner.hasMasterKeyDigest(testConfig1.masterKeyDigestId))

            // Perform removal
            useCase(testConfig1.id)

            // Verify config is removed from repository
            val configs = repository.getAllConfigs().first()
            assertTrue(configs.isEmpty(), "Repository should be empty after removal")

            // Verify key digest is removed from hmacSigner
            val hasKey = hmacSigner.hasMasterKeyDigest(testConfig1.masterKeyDigestId)
            assertFalse(
                hasKey,
                "HmacSigner should no longer have master key digest for removed config ID"
            )

            // Verify attempt to sign with removed key throws SigningKeyNotFoundException
            assertFailsWith<SigningKeyNotFoundException> {
                hmacSigner.sign(testConfig1.masterKeyDigestId, "input")
            }
        }

    @Test
    fun `invoke should selectively remove only the specified config when multiple configs exist`() =
        runTest {
            repository.addConfig(testConfig1)
            repository.addConfig(testConfig2)
            hmacSigner.registerKey(testConfig1.masterKeyDigestId, masterKey1)
            hmacSigner.registerKey(testConfig2.masterKeyDigestId, masterKey2)

            // Remove config 1
            useCase(testConfig1.id)

            // Verify config 1 is gone, config 2 remains
            val configs = repository.getAllConfigs().first()
            assertEquals(1, configs.size)
            assertEquals(testConfig2, configs.first())

            assertFalse(hmacSigner.hasMasterKeyDigest(testConfig1.masterKeyDigestId))
            assertTrue(hmacSigner.hasMasterKeyDigest(testConfig2.masterKeyDigestId))
        }

    @Test
    fun `invoke with non-existent configId should execute cleanly without error`() = runTest {
        repository.addConfig(testConfig1)
        hmacSigner.registerKey(testConfig1.masterKeyDigestId, masterKey1)

        // Attempt to remove non-existent config ID 999
        useCase(999)

        // Verify original config and key remain untouched
        val configs = repository.getAllConfigs().first()
        assertEquals(1, configs.size)
        assertEquals(testConfig1, configs.first())
        assertTrue(hmacSigner.hasMasterKeyDigest(testConfig1.masterKeyDigestId))
    }

    @Test
    fun `invoke should propagate exception when repository removeById fails and skip delete key in signer`() =
        runTest {
            hmacSigner.registerKey(testConfig1.masterKeyDigestId, masterKey1)

            val failingRepo = object : KdfPassGenConfigRepository {
                override suspend fun getConfigById(configId: Long) = testConfig1
                override suspend fun addConfig(config: KdfPassGenConfig) = testConfig1.id
                override suspend fun removeConfigById(configId: Long) {
                    throw IllegalStateException("Database delete failed")
                }

                override fun getAllConfigs() = repository.getAllConfigs()
                override suspend fun deleteAllConfigs() = repository.deleteAllConfigs()
                override suspend fun doesMasterKeyDigestIdExist(masterKeyDigestId: String) =
                    repository.doesMasterKeyDigestIdExist(masterKeyDigestId)

                override suspend fun getAllMasterKeyDigestIds() =
                    repository.getAllMasterKeyDigestIds()
            }

            val failingUseCase = RemoveKdfPassGenConfigUseCase(failingRepo, hmacSigner)

            val exception = assertFailsWith<IllegalStateException> {
                failingUseCase(testConfig1.id)
            }

            assertEquals("Database delete failed", exception.message)
            assertFalse(
                hmacSigner.hasMasterKeyDigest(testConfig1.masterKeyDigestId),
                "Signer should have deleted the key digest even if repository deletion failed."
            )
        }

    @Test
    fun `invoke should propagate exception when hmacSigner deleteMasterKeyDigest fails`() =
        runTest {
            repository.addConfig(testConfig1)

            val failingSigner = object : HmacSigner {
                override suspend fun registerKey(mkdId: String, rawMasterKey: ByteArray) = true
                override suspend fun sign(mkdId: String, input: String): ByteArray = ByteArray(0)
                override suspend fun hasMasterKeyDigest(mkdId: String): Boolean = true
                override suspend fun deleteMasterKeyDigest(mkdId: String) {
                    throw IllegalArgumentException("Key deletion failed")
                }
            }

            val failingUseCase = RemoveKdfPassGenConfigUseCase(repository, failingSigner)

            val exception = assertFailsWith<IllegalArgumentException> {
                failingUseCase(testConfig1.id)
            }

            assertEquals("Key deletion failed", exception.message)
            assertTrue(repository.getAllConfigs().first().isNotEmpty())
        }
}
