package ir.ornix.passgen.core.domain

import kotlinx.coroutines.flow.Flow

interface LocalAuthRepository {
    fun getLocalAuthType(): Flow<LocalAuthType>
    suspend fun setLocalAuthType(type: LocalAuthType)

    suspend fun saveEncryptedSecret(encryptedSecret: ByteArray)
    suspend fun getEncryptedSecret(): ByteArray?

    fun isBiometricEnabled(): Flow<Boolean>
    suspend fun setBiometricEnabled(enabled: Boolean)

    suspend fun clearAuth()
}
