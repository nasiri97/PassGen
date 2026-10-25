package ir.ornix.passgen.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.db.SqlDriver
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.LocalAuthType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SqlDelightLocalAuthRepository(
    database: PassGenDatabase,
    private val driver: SqlDriver,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : LocalAuthRepository {

    private val queries = database.localAuthQueries
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)


    override fun getLocalAuthType(): Flow<LocalAuthType> {
        return queries.selectLocalAuth()
            .asFlow()
            .mapToOne(ioDispatcher)
            .map {
                LocalAuthType.valueOf(it.auth_type)
            }
    }

    override fun setLocalAuthType(type: LocalAuthType) {
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
                queries.updateAuthType(type.name)
            } catch (_: Exception) {
            }
        }
    }

    override fun saveEncryptedSecret(encryptedSecret: ByteArray) {
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
                queries.updateEncryptedSecret(encryptedSecret)
            } catch (_: Exception) {
            }
        }
    }

    override suspend fun getEncryptedSecret(): ByteArray? {
        return queries.selectLocalAuth().awaitAsOne().encrypted_secret
    }

    override fun isBiometricEnabled(): Flow<Boolean> {
        return queries.selectLocalAuth()
            .asFlow()
            .mapToOne(ioDispatcher)
            .map {
                it.is_biometric_enabled == 1L
            }
    }

    override fun setBiometricEnabled(enabled: Boolean) {
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
                queries.updateBiometricEnabled(if (enabled) 1L else 0L)
            } catch (_: Exception) {
            }
        }
    }

    override fun clearAuth() {
        scope.launch {
            try {
                PassGenDatabase.Schema.create(driver).await()
                queries.clearLocalAuth()
            } catch (_: Exception) {
            }
        }
    }
}
