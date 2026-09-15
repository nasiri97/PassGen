package ir.ornix.passgen.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.LocalAuthType

class SettingsLocalAuthRepository(private val settings: Settings = Settings()) : LocalAuthRepository {
    companion object {
        private const val KEY_AUTH_TYPE = "local_auth_type"
        private const val KEY_SECRET_HASH = "local_auth_secret_hash"
        private const val KEY_BIOMETRIC_ENABLED = "local_auth_biometric_enabled"
        private const val KEY_SETUP_COMPLETED = "local_auth_setup_completed"
    }

    override fun getLocalAuthType(): LocalAuthType {
        val name = settings.getStringOrNull(KEY_AUTH_TYPE) ?: return LocalAuthType.NONE
        return try {
            LocalAuthType.valueOf(name)
        } catch (e: Exception) {
            LocalAuthType.NONE
        }
    }

    override fun setLocalAuthType(type: LocalAuthType) {
        settings[KEY_AUTH_TYPE] = type.name
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

    override fun clearAuth() {
        settings.remove(KEY_AUTH_TYPE)
        settings.remove(KEY_SECRET_HASH)
        settings.remove(KEY_BIOMETRIC_ENABLED)
        settings.remove(KEY_SETUP_COMPLETED)
    }

    override fun isBiometricEnabled(): Boolean {
        return settings.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    override fun setBiometricEnabled(enabled: Boolean) {
        settings[KEY_BIOMETRIC_ENABLED] = enabled
    }

    override fun isSetupCompleted(): Boolean {
        return settings.getBoolean(KEY_SETUP_COMPLETED, false)
    }

    override fun setSetupCompleted(completed: Boolean) {
        settings[KEY_SETUP_COMPLETED] = completed
    }
}
