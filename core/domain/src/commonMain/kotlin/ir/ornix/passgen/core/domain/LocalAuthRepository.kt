package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.localauth.LocalAuthType
import kotlinx.coroutines.flow.Flow

interface LocalAuthRepository {
    fun getLocalAuthType(): Flow<LocalAuthType>
    suspend fun setLocalAuthType(type: LocalAuthType)

    suspend fun saveEncryptedSecret(encryptedSecret: ByteArray)
    suspend fun getEncryptedSecret(): ByteArray?

    fun isBiometricEnabled(): Flow<Boolean>
    suspend fun setBiometricEnabled(enabled: Boolean)

    fun getFailedAuthAttemptCount(): Flow<Long>
    suspend fun incrementFailedAuthAttemptCount()
    suspend fun resetFailedAuthAttemptCount()

    fun getFailedAuthAttemptLimit(): Flow<Long>
    suspend fun updateFailedAuthAttemptLimit(maxAttempts: Long)
    suspend fun disableFailedAuthAttemptLimit()

    suspend fun clearAuth()
}
