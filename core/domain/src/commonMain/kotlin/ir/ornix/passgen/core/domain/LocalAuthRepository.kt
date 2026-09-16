package ir.ornix.passgen.core.domain

import kotlinx.coroutines.flow.StateFlow

interface LocalAuthRepository {
    fun getLocalAuthType(): StateFlow<LocalAuthType>
    fun setLocalAuthType(type: LocalAuthType)

    fun saveSecretHash(hash: String)
    fun validateSecretHash(hash: String): Boolean
    
    fun isBiometricEnabled(): StateFlow<Boolean>
    fun setBiometricEnabled(enabled: Boolean)

    fun clearAuth()
}
