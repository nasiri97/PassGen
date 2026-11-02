package ir.ornix.passgen.core.domain.data_management

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.domain.CredentialRepository
import ir.ornix.passgen.core.domain.FakeHmacSigner
import ir.ornix.passgen.core.domain.FakePassGenConfigRepository
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.Password.Companion.toPassword
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PurgeSensitiveDataUseCaseTest {

    private lateinit var hmacSigner: FakeHmacSigner
    private lateinit var passGenConfigRepository: FakePassGenConfigRepository
    private lateinit var credentialRepository: FakeCredentialRepository
    private lateinit var localAuthRepository: FakeLocalAuthRepository
    private lateinit var useCase: PurgeSensitiveDataUseCase

    private class FakeCredentialRepository : CredentialRepository {
        private val credentials = MutableStateFlow<List<Credential>>(emptyList())

        override suspend fun save(credential: Credential) {
            credentials.value += credential
        }

        override suspend fun delete(credentialId: Long) {
            credentials.value = credentials.value.filterNot { it.id == credentialId }
        }

        override fun getAll(): Flow<List<Credential>> = credentials.asStateFlow()

        override suspend fun deleteAllCredentials() {
            credentials.value = emptyList()
        }
    }

    private class FakeLocalAuthRepository : LocalAuthRepository {
        private val authType = MutableStateFlow(LocalAuthType.PIN)
        private val encryptedSecret = MutableStateFlow<ByteArray?>(byteArrayOf(1, 2, 3))
        private val biometricEnabled = MutableStateFlow(true)

        override fun getLocalAuthType(): Flow<LocalAuthType> = authType.asStateFlow()
        override suspend fun setLocalAuthType(type: LocalAuthType) {
            authType.value = type
        }

        override suspend fun saveEncryptedSecret(encryptedSecret: ByteArray) {
            this.encryptedSecret.value = encryptedSecret
        }

        override suspend fun getEncryptedSecret(): ByteArray? = encryptedSecret.value
        override fun isBiometricEnabled(): Flow<Boolean> = biometricEnabled.asStateFlow()
        override suspend fun setBiometricEnabled(enabled: Boolean) {
            biometricEnabled.value = enabled
        }

        override suspend fun clearAuth() {
            authType.value = LocalAuthType.NONE
            encryptedSecret.value = null
            biometricEnabled.value = false
        }
    }

    private val testConfig = KdfPassGenConfig(
        id = 123,
        name = "Test KDF Config",
        preprocessConfig = PreprocessConfig(
            trimLeadingAndTrailingSpaces = true,
            collapseMultipleSpaces = false,
            convertToLowercase = false
        ),
        inputHasher = InputHasher.SHA256,
        passEncoder = StringPassEncoder.HexPassEncoder,
        passwordLength = 16
    )

    private val testCredential = Credential(
        id = 1,
        title = "GitHub",
        username = "user",
        password = "Password123!".toPassword(16),
        email = "test@example.com",
        associatedPackageNames = emptySet(),
        associatedWebDomains = setOf("github.com")
    )

    @BeforeTest
    fun setup() {
        hmacSigner = FakeHmacSigner()
        passGenConfigRepository = FakePassGenConfigRepository()
        credentialRepository = FakeCredentialRepository()
        localAuthRepository = FakeLocalAuthRepository()

        useCase = PurgeSensitiveDataUseCase(
            hmacSigner = hmacSigner,
            passGenConfigRepository = passGenConfigRepository,
            credentialRepository = credentialRepository,
            localAuthRepository = localAuthRepository
        )
    }

    @Test
    fun `invoke should delete all master key digests, configs, credentials, and clear local auth`() =
        runTest {
            // Setup initial state
            passGenConfigRepository.add(testConfig)
            hmacSigner.registerKey("${testConfig.id}", "master_key".encodeToByteArray())
            credentialRepository.save(testCredential)

            // Verify initial state
            assertTrue(hmacSigner.hasMasterKeyDigest("${testConfig.id}"))
            assertEquals(1, passGenConfigRepository.getAllKdfPassGenConfigs().first().size)
            assertEquals(1, credentialRepository.getAll().first().size)
            assertEquals(LocalAuthType.PIN, localAuthRepository.getLocalAuthType().first())
            assertTrue(localAuthRepository.isBiometricEnabled().first())

            // Execute purge
            useCase()

            // Verify all sensitive data is purged
            assertFalse(hmacSigner.hasMasterKeyDigest("${testConfig.id}"))
            assertTrue(passGenConfigRepository.getAllKdfPassGenConfigs().first().isEmpty())
            assertTrue(credentialRepository.getAll().first().isEmpty())
            assertEquals(LocalAuthType.NONE, localAuthRepository.getLocalAuthType().first())
            assertFalse(localAuthRepository.isBiometricEnabled().first())
        }
}
