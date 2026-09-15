package ir.ornix.passgen.core.domain

import kotlinx.coroutines.flow.Flow

interface LocalAuthRepository {
    fun getLocalAuthType(): LocalAuthType
    fun setLocalAuthType(type: LocalAuthType)
    fun saveSecretHash(hash: String)
    fun validateSecretHash(hash: String): Boolean
    fun clearAuth()
    
    fun isBiometricEnabled(): Boolean
    fun setBiometricEnabled(enabled: Boolean)

    fun isSetupCompleted(): Boolean
    fun setSetupCompleted(completed: Boolean)
}
