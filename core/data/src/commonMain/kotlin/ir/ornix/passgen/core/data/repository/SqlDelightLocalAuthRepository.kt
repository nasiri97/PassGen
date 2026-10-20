package ir.ornix.passgen.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.db.SqlDriver
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.LocalAuthType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SqlDelightLocalAuthRepository(
    database: PassGenDatabase,
    private val driver: SqlDriver,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : LocalAuthRepository {

    private val queries = database.localAuthQueries
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    private val localAuthTypeFlow = MutableStateFlow(LocalAuthType.NONE)
    private val biometricEnabledFlow = MutableStateFlow(false)
    private var encryptedSecretVal: ByteArray? = null

    init {
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
            } catch (_: Exception) {
            }

            try {
                val entity = queries.selectLocalAuth().awaitAsOneOrNull()
                if (entity != null) {
                    localAuthTypeFlow.value = try {
                        LocalAuthType.valueOf(entity.auth_type)
                    } catch (_: Exception) {
                        LocalAuthType.NONE
                    }
                    biometricEnabledFlow.value = entity.is_biometric_enabled == 1L
                    encryptedSecretVal = entity.encrypted_secret
                }
            } catch (_: Exception) {
            }
        }
    }

    override fun getLocalAuthType(): StateFlow<LocalAuthType> = localAuthTypeFlow.asStateFlow()

    override fun setLocalAuthType(type: LocalAuthType) {
        localAuthTypeFlow.value = type
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
                queries.updateAuthType(type.name)
            } catch (_: Exception) {
            }
        }
    }

    override fun saveEncryptedSecret(encryptedSecret: ByteArray) {
        encryptedSecretVal = encryptedSecret
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
                queries.updateEncryptedSecret(encryptedSecret)
            } catch (_: Exception) {
            }
        }
    }

    override fun getEncryptedSecret(): ByteArray {
        return encryptedSecretVal ?: ByteArray(0)
    }

    override fun isBiometricEnabled(): StateFlow<Boolean> = biometricEnabledFlow.asStateFlow()

    override fun setBiometricEnabled(enabled: Boolean) {
        biometricEnabledFlow.value = enabled
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
                queries.updateBiometricEnabled(if (enabled) 1L else 0L)
            } catch (_: Exception) {
            }
        }
    }

    override fun clearAuth() {
        localAuthTypeFlow.value = LocalAuthType.NONE
        biometricEnabledFlow.value = false
        encryptedSecretVal = null
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
                queries.clearLocalAuth()
            } catch (_: Exception) {
            }
        }
    }
}
