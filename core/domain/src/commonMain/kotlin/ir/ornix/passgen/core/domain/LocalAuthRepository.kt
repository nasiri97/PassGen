package ir.ornix.passgen.core.domain

import kotlinx.coroutines.flow.Flow

interface LocalAuthRepository {
    fun getLocalAuthType(): Flow<LocalAuthType>
    fun setLocalAuthType(type: LocalAuthType)

    fun saveEncryptedSecret(encryptedSecret: ByteArray)
    suspend fun getEncryptedSecret(): ByteArray?

    fun isBiometricEnabled(): Flow<Boolean>
    fun setBiometricEnabled(enabled: Boolean)

    fun clearAuth()
}
