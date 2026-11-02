package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.CredentialRepository
import ir.ornix.passgen.core.domain.Crypto
import ir.ornix.passgen.core.domain.FakeHmacSigner
import ir.ornix.passgen.core.domain.FakePassGenConfigRepository
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.data_management.PurgeSensitiveDataUseCase
import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.localauth.LocalAuthType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidateLocalAuthSecretUseCaseTest {

    private lateinit var crypto: Crypto
    private lateinit var getLocalAuthType: GetLocalAuthTypeUseCase
    private lateinit var purgeSensitiveData: PurgeSensitiveDataUseCase
    private lateinit var useCase: ValidateLocalAuthSecretUseCase

    private class FakeCrypto : Crypto {
        override fun encrypt(data: ByteArray): ByteArray = data
        override fun decrypt(data: ByteArray): ByteArray = data
    }

    private class FakeLocalAuthRepository(
        private var encryptedSecretValue: ByteArray? = null,
        private var authType: LocalAuthType = LocalAuthType.NONE,
        private val attemptCount: MutableStateFlow<Long> = MutableStateFlow(0L),
        private val attemptLimit: MutableStateFlow<Long> = MutableStateFlow(Long.MAX_VALUE)
    ) : LocalAuthRepository {
        var clearAuthCalled = false
        var incrementAttemptCountCalled = false
        var resetAttemptCountCalled = false

        override fun getLocalAuthType(): Flow<LocalAuthType> =
            MutableStateFlow(authType).asStateFlow()

        override suspend fun setLocalAuthType(type: LocalAuthType) {
            authType = type
        }

        override suspend fun saveEncryptedSecret(encryptedSecret: ByteArray) {
            encryptedSecretValue = encryptedSecret
        }

        override suspend fun getEncryptedSecret(): ByteArray? = encryptedSecretValue
        override fun isBiometricEnabled(): Flow<Boolean> = MutableStateFlow(false).asStateFlow()
        override suspend fun setBiometricEnabled(enabled: Boolean) {}

        override fun getFailedAuthAttemptCount(): Flow<Long> = attemptCount.asStateFlow()
        override suspend fun incrementFailedAuthAttemptCount() {
            incrementAttemptCountCalled = true
            attemptCount.value++
        }

        override suspend fun resetFailedAuthAttemptCount() {
            resetAttemptCountCalled = true
            attemptCount.value = 0L
        }

        override fun getFailedAuthAttemptLimit(): Flow<Long> = attemptLimit.asStateFlow()
        override suspend fun updateFailedAuthAttemptLimit(maxAttempts: Long) {
            attemptLimit.value = maxAttempts
        }

        override suspend fun disableFailedAuthAttemptLimit() {
            attemptLimit.value = Long.MAX_VALUE
        }

        override suspend fun clearAuth() {
            clearAuthCalled = true
            authType = LocalAuthType.NONE
            encryptedSecretValue = null
            attemptCount.value = 0L
            attemptLimit.value = Long.MAX_VALUE
        }
    }

    private class FakeCredentialRepository : CredentialRepository {
        override suspend fun save(credential: Credential) {}
        override suspend fun delete(credentialId: Long) {}
        override fun getAll(): Flow<List<Credential>> =
            MutableStateFlow<List<Credential>>(emptyList()).asStateFlow()

        override suspend fun deleteAllCredentials() {}
    }

    @BeforeTest
    fun setup() {
        crypto = FakeCrypto()
    }

    @Test
    fun `invoke returns false and does not purge when encrypted secret is null and auth type is NONE`() =
        runTest {
            val repo = FakeLocalAuthRepository(
                encryptedSecretValue = null,
                authType = LocalAuthType.NONE
            )
            getLocalAuthType = GetLocalAuthTypeUseCase(repo)
            purgeSensitiveData = PurgeSensitiveDataUseCase(
                hmacSigner = FakeHmacSigner(),
                passGenConfigRepository = FakePassGenConfigRepository(),
                credentialRepository = FakeCredentialRepository(),
                localAuthRepository = repo
            )

            useCase = ValidateLocalAuthSecretUseCase(
                crypto,
                repo,
                getLocalAuthType,
                purgeSensitiveData,
                IsFailedAuthAttemptLimitExceededUseCase(repo)
            )

            val secret = "1234".encodeToByteArray()
            val isValid = useCase(secret)

            assertFalse(isValid)
            assertFalse(repo.clearAuthCalled)
        }

    @Test
    fun `invoke returns false and triggers purge when encrypted secret is null and auth type is PIN`() =
        runTest {
            val repo =
                FakeLocalAuthRepository(encryptedSecretValue = null, authType = LocalAuthType.PIN)
            getLocalAuthType = GetLocalAuthTypeUseCase(repo)
            purgeSensitiveData = PurgeSensitiveDataUseCase(
                hmacSigner = FakeHmacSigner(),
                passGenConfigRepository = FakePassGenConfigRepository(),
                credentialRepository = FakeCredentialRepository(),
                localAuthRepository = repo
            )

            useCase = ValidateLocalAuthSecretUseCase(
                crypto,
                repo,
                getLocalAuthType,
                purgeSensitiveData,
                IsFailedAuthAttemptLimitExceededUseCase(repo)
            )

            val secret = "1234".encodeToByteArray()
            val isValid = useCase(secret)

            assertFalse(isValid)
            assertTrue(repo.clearAuthCalled)
        }

    @Test
    fun `invoke returns true when encrypted secret exists and secret matches`() = runTest {
        val correctSecret = "secret123".encodeToByteArray()
        val repo = FakeLocalAuthRepository(
            encryptedSecretValue = correctSecret,
            authType = LocalAuthType.PASSWORD
        )
        getLocalAuthType = GetLocalAuthTypeUseCase(repo)
        purgeSensitiveData = PurgeSensitiveDataUseCase(
            hmacSigner = FakeHmacSigner(),
            passGenConfigRepository = FakePassGenConfigRepository(),
            credentialRepository = FakeCredentialRepository(),
            localAuthRepository = repo
        )
        useCase = ValidateLocalAuthSecretUseCase(
            crypto,
            repo,
            getLocalAuthType,
            purgeSensitiveData,
            IsFailedAuthAttemptLimitExceededUseCase(repo)
        )

        val isValid = useCase("secret123".encodeToByteArray())

        assertTrue(isValid)
        assertFalse(repo.clearAuthCalled)
    }

    @Test
    fun `invoke returns false when encrypted secret exists and secret does not match`() = runTest {
        val correctSecret = "secret123".encodeToByteArray()
        val repo = FakeLocalAuthRepository(
            encryptedSecretValue = correctSecret,
            authType = LocalAuthType.PASSWORD
        )
        getLocalAuthType = GetLocalAuthTypeUseCase(repo)
        purgeSensitiveData = PurgeSensitiveDataUseCase(
            hmacSigner = FakeHmacSigner(),
            passGenConfigRepository = FakePassGenConfigRepository(),
            credentialRepository = FakeCredentialRepository(),
            localAuthRepository = repo
        )

        useCase = ValidateLocalAuthSecretUseCase(
            crypto,
            repo,
            getLocalAuthType,
            purgeSensitiveData,
            IsFailedAuthAttemptLimitExceededUseCase(repo)
        )

        val isValid = useCase("wrongsecret".encodeToByteArray())

        assertFalse(isValid)
        assertFalse(repo.clearAuthCalled)
    }
}
