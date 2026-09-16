package ir.ornix.passgen.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.LocalAuthType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsLocalAuthRepository(private val settings: Settings = Settings()) :
    LocalAuthRepository {

    companion object {
        private const val KEY_AUTH_TYPE = "local_auth_type"
        private const val KEY_SECRET_HASH = "local_auth_secret_hash"
        private const val KEY_BIOMETRIC_ENABLED = "local_auth_biometric_enabled"

        private const val DEFAULT_IS_BIOMETRIC_ENABLED = false
        private val DEFAULT_LOCAL_AUTH_TYPE = LocalAuthType.NONE
    }


    private val isBiometricEnabled =
        MutableStateFlow<Boolean>(
            settings.getBoolean(KEY_BIOMETRIC_ENABLED, DEFAULT_IS_BIOMETRIC_ENABLED)
        )

    private val localAuthType =
        MutableStateFlow<LocalAuthType>(run {
            val name = settings.getStringOrNull(KEY_AUTH_TYPE) ?: return@run DEFAULT_LOCAL_AUTH_TYPE
            try {
                LocalAuthType.valueOf(name)
            } catch (e: Exception) {
                DEFAULT_LOCAL_AUTH_TYPE
            }
        })

    override fun getLocalAuthType(): StateFlow<LocalAuthType> {
        return localAuthType
    }

    override fun setLocalAuthType(type: LocalAuthType) {
        settings[KEY_AUTH_TYPE] = type.name
        localAuthType.value = type
    }

    override fun saveSecretHash(hash: String) {
        val encrypted = PlatformCrypto.encrypt(hash)
        settings[KEY_SECRET_HASH] = encrypted
    }

    override fun validateSecretHash(hash: String): Boolean {
        val stored = settings.getStringOrNull(KEY_SECRET_HASH) ?: return false
        val decrypted = PlatformCrypto.decrypt(stored)
        return decrypted == hash
    }

    override fun isBiometricEnabled(): StateFlow<Boolean> {
        return isBiometricEnabled
    }

    override fun setBiometricEnabled(enabled: Boolean) {
        settings[KEY_BIOMETRIC_ENABLED] = enabled
        isBiometricEnabled.value = enabled
    }

    override fun clearAuth() {
        settings.remove(KEY_AUTH_TYPE)
        settings.remove(KEY_SECRET_HASH)
        settings.remove(KEY_BIOMETRIC_ENABLED)

        localAuthType.value = DEFAULT_LOCAL_AUTH_TYPE
        isBiometricEnabled.value = DEFAULT_IS_BIOMETRIC_ENABLED
    }
}
