package ir.ornix.passgen.core.domain

import kotlinx.coroutines.flow.StateFlow

interface LocalAuthRepository {
    fun getLocalAuthType(): StateFlow<LocalAuthType>
    fun setLocalAuthType(type: LocalAuthType)

    fun saveEncryptedSecret(encryptedSecret: ByteArray)
    fun getEncryptedSecret(): ByteArray

    fun isBiometricEnabled(): StateFlow<Boolean>
    fun setBiometricEnabled(enabled: Boolean)

    fun clearAuth()
}
