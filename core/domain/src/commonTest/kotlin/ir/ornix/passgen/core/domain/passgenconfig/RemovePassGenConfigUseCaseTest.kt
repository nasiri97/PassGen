package ir.ornix.passgen.core.domain.passgenconfig

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.domain.FakeHmacSigner
import ir.ornix.passgen.core.domain.FakePassGenConfigRepository
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.domain.SigningKeyNotFoundException
import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import ir.ornix.passgen.core.domain.passgenconfig.model.PreprocessConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RemovePassGenConfigUseCaseTest {

    private lateinit var repository: FakePassGenConfigRepository
    private lateinit var hmacSigner: FakeHmacSigner
    private lateinit var useCase: RemovePassGenConfigUseCase

    private val masterKey1 = "master_key_1".encodeToByteArray()
    private val masterKey2 = "master_key_2".encodeToByteArray()

    private val testConfig1 = KDFPassGenConfig(
        id = 100,
        name = "Config 100",
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
        id = 200,
        name = "Config 200",
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
        useCase = RemovePassGenConfigUseCase(repository, hmacSigner)
    }

    @Test
    fun `invoke should remove existing config from repository and key digest from hmacSigner`() =
        runTest {
            repository.add(testConfig1)
            hmacSigner.registerKey("${testConfig1.id}", masterKey1)

            // Verify initial state
            assertEquals(1, repository.getAll().first().size)
            assertTrue(hmacSigner.hasMasterKeyDigest("${testConfig1.id}"))

            // Perform removal
            useCase(testConfig1.id)

            // Verify config is removed from repository
            val configs = repository.getAll().first()
            assertTrue(configs.isEmpty(), "Repository should be empty after removal")

            // Verify key digest is removed from hmacSigner
            val hasKey = hmacSigner.hasMasterKeyDigest("${testConfig1.id}")
            assertFalse(
                hasKey,
                "HmacSigner should no longer have master key digest for removed config ID"
            )

            // Verify attempt to sign with removed key throws SigningKeyNotFoundException
            assertFailsWith<SigningKeyNotFoundException> {
                hmacSigner.sign("${testConfig1.id}", "input")
            }
        }

    @Test
    fun `invoke should selectively remove only the specified config when multiple configs exist`() =
        runTest {
            repository.add(testConfig1)
            repository.add(testConfig2)
            hmacSigner.registerKey("${testConfig1.id}", masterKey1)
            hmacSigner.registerKey("${testConfig2.id}", masterKey2)

            // Remove config 1
            useCase(testConfig1.id)

            // Verify config 1 is gone, config 2 remains
            val configs = repository.getAll().first()
            assertEquals(1, configs.size)
            assertEquals(testConfig2, configs.first())

            assertFalse(hmacSigner.hasMasterKeyDigest("${testConfig1.id}"))
            assertTrue(hmacSigner.hasMasterKeyDigest("${testConfig2.id}"))
        }

    @Test
    fun `invoke with non-existent configId should execute cleanly without error`() = runTest {
        repository.add(testConfig1)
        hmacSigner.registerKey("${testConfig1.id}", masterKey1)

        // Attempt to remove non-existent config ID 999
        useCase(999)

        // Verify original config and key remain untouched
        val configs = repository.getAll().first()
        assertEquals(1, configs.size)
        assertEquals(testConfig1, configs.first())
        assertTrue(hmacSigner.hasMasterKeyDigest("${testConfig1.id}"))
    }

    @Test
    fun `invoke should propagate exception when repository removeById fails and skip delete key in signer`() =
        runTest {
            hmacSigner.registerKey("${testConfig1.id}", masterKey1)

            val failingRepo = object : PassGenConfigRepository {
                override suspend fun add(config: KDFPassGenConfig): Int = config.id
                override suspend fun removeById(configId: Int) {
                    throw IllegalStateException("Database delete failed")
                }

                override fun getAll(): Flow<List<KDFPassGenConfig>> = repository.getAll()
            }

            val failingUseCase = RemovePassGenConfigUseCase(failingRepo, hmacSigner)

            val exception = assertFailsWith<IllegalStateException> {
                failingUseCase(testConfig1.id)
            }

            assertEquals("Database delete failed", exception.message)
            assertTrue(
                hmacSigner.hasMasterKeyDigest("${testConfig1.id}"),
                "Signer should still retain key digest if repo deletion failed"
            )
        }

    @Test
    fun `invoke should propagate exception when hmacSigner deleteMasterKeyDigest fails`() =
        runTest {
            repository.add(testConfig1)

            val failingSigner = object : HmacSigner {
                override suspend fun registerKey(mkdId: String, rawMasterKey: ByteArray) {}
                override suspend fun sign(mkdId: String, input: String): ByteArray = ByteArray(0)
                override suspend fun hasMasterKeyDigest(mkdId: String): Boolean = true
                override suspend fun deleteMasterKeyDigest(mkdId: String) {
                    throw IllegalArgumentException("Key deletion failed")
                }
            }

            val failingUseCase = RemovePassGenConfigUseCase(repository, failingSigner)

            val exception = assertFailsWith<IllegalArgumentException> {
                failingUseCase(testConfig1.id)
            }

            assertEquals("Key deletion failed", exception.message)
            // Note: repo removal succeeded first
            assertTrue(repository.getAll().first().isEmpty())
        }
}
