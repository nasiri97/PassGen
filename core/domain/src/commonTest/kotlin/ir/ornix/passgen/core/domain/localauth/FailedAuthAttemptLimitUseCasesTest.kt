package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.model.localauth.FailedAuthAttemptLimit
import ir.ornix.passgen.core.model.localauth.LocalAuthType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FailedAuthAttemptLimitUseCasesTest {

    private class FakeLocalAuthRepository : LocalAuthRepository {
        val attemptCountFlow = MutableStateFlow(0L)
        val attemptLimitFlow = MutableStateFlow(Long.MAX_VALUE)

        override fun getLocalAuthType(): Flow<LocalAuthType> =
            MutableStateFlow(LocalAuthType.NONE).asStateFlow()

        override suspend fun setLocalAuthType(type: LocalAuthType) {}
        override suspend fun saveEncryptedSecret(encryptedSecret: ByteArray) {}
        override suspend fun getEncryptedSecret(): ByteArray? = null
        override fun isBiometricEnabled(): Flow<Boolean> = MutableStateFlow(false).asStateFlow()
        override suspend fun setBiometricEnabled(enabled: Boolean) {}

        override fun getFailedAuthAttemptCount(): Flow<Long> = attemptCountFlow.asStateFlow()
        override suspend fun incrementFailedAuthAttemptCount() {
            attemptCountFlow.value++
        }

        override suspend fun resetFailedAuthAttemptCount() {
            attemptCountFlow.value = 0L
        }

        override fun getFailedAuthAttemptLimit(): Flow<Long> = attemptLimitFlow.asStateFlow()
        override suspend fun updateFailedAuthAttemptLimit(maxAttempts: Long) {
            attemptLimitFlow.value = maxAttempts
        }

        override suspend fun disableFailedAuthAttemptLimit() {
            attemptLimitFlow.value = Long.MAX_VALUE
        }

        override suspend fun clearAuth() {
            attemptCountFlow.value = 0L
            attemptLimitFlow.value = Long.MAX_VALUE
        }
    }

    @Test
    fun testGetFailedAuthAttemptLimitUseCase() = runTest {
        val repository = FakeLocalAuthRepository()
        val getUseCase = GetFailedAuthAttemptLimitUseCase(repository)

        assertEquals(FailedAuthAttemptLimit.DISABLED, getUseCase().first())

        repository.attemptLimitFlow.value = 3L
        assertEquals(FailedAuthAttemptLimit.THREE_ATTEMPTS, getUseCase().first())

        repository.attemptLimitFlow.value = 5L
        assertEquals(FailedAuthAttemptLimit.FIVE_ATTEMPTS, getUseCase().first())

        repository.attemptLimitFlow.value = 1L
        assertEquals(FailedAuthAttemptLimit.ONE_ATTEMPT, getUseCase().first())
    }

    @Test
    fun testUpdateFailedAuthAttemptLimitUseCase() = runTest {
        val repository = FakeLocalAuthRepository()
        val updateUseCase = UpdateFailedAuthAttemptLimitUseCase(repository)

        updateUseCase(FailedAuthAttemptLimit.THREE_ATTEMPTS)
        assertEquals(3L, repository.attemptLimitFlow.value)

        updateUseCase(FailedAuthAttemptLimit.FIVE_ATTEMPTS)
        assertEquals(5L, repository.attemptLimitFlow.value)

        updateUseCase(FailedAuthAttemptLimit.DISABLED)
        assertEquals(Long.MAX_VALUE, repository.attemptLimitFlow.value)
    }
}
