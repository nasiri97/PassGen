package ir.ornix.passgen.core.data.repository

import ir.ornix.passgen.core.database.LocalAuthEntity
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.LocalAuthType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SqlDelightLocalAuthRepository(
    database: PassGenDatabase
) : LocalAuthRepository {

    private val queries = database.localAuthQueries

    private val initialEntity: LocalAuthEntity = queries.selectLocalAuth().executeAsOneOrNull()
        ?: LocalAuthEntity(
            id = 1,
            auth_type = LocalAuthType.NONE.name,
            encrypted_secret = null,
            is_biometric_enabled = 0
        )

    private val localAuthTypeFlow = MutableStateFlow(
        try {
            LocalAuthType.valueOf(initialEntity.auth_type)
        } catch (_: Exception) {
            LocalAuthType.NONE
        }
    )

    private val biometricEnabledFlow = MutableStateFlow(
        initialEntity.is_biometric_enabled == 1L
    )

    override fun getLocalAuthType(): StateFlow<LocalAuthType> = localAuthTypeFlow.asStateFlow()

    override fun setLocalAuthType(type: LocalAuthType) {
        queries.updateAuthType(type.name)
        localAuthTypeFlow.value = type
    }

    override fun saveEncryptedSecret(encryptedSecret: ByteArray) {
        queries.updateEncryptedSecret(encryptedSecret)
    }

    override fun getEncryptedSecret(): ByteArray {
        return queries.selectLocalAuth().executeAsOneOrNull()?.encrypted_secret ?: ByteArray(0)
    }

    override fun isBiometricEnabled(): StateFlow<Boolean> = biometricEnabledFlow.asStateFlow()

    override fun setBiometricEnabled(enabled: Boolean) {
        queries.updateBiometricEnabled(if (enabled) 1L else 0L)
        biometricEnabledFlow.value = enabled
    }

    override fun clearAuth() {
        queries.clearLocalAuth()
        localAuthTypeFlow.value = LocalAuthType.NONE
        biometricEnabledFlow.value = false
    }
}
