package ir.ornix.passgen.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.LocalAuthType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightLocalAuthRepository(
    database: PassGenDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : LocalAuthRepository {

    private val queries = database.localAuthQueries

    override fun getLocalAuthType(): Flow<LocalAuthType> {
        return queries.selectLocalAuth()
            .asFlow()
            .mapToOne(dispatcher)
            .map {
                LocalAuthType.valueOf(it.auth_type)
            }
    }

    override suspend fun setLocalAuthType(type: LocalAuthType): Unit =
        withContext(dispatcher) {
            queries.updateAuthType(type.name)
        }

    override suspend fun saveEncryptedSecret(encryptedSecret: ByteArray): Unit =
        withContext(dispatcher) {
            queries.updateEncryptedSecret(encryptedSecret)
        }

    override suspend fun getEncryptedSecret(): ByteArray? =
        withContext(dispatcher) {
            queries.selectLocalAuth().awaitAsOne().encrypted_secret
        }

    override fun isBiometricEnabled(): Flow<Boolean> {
        return queries.selectLocalAuth()
            .asFlow()
            .mapToOne(dispatcher)
            .map {
                it.is_biometric_enabled == 1L
            }
    }

    override suspend fun setBiometricEnabled(enabled: Boolean): Unit =
        withContext(dispatcher) {
            queries.updateBiometricEnabled(if (enabled) 1L else 0L)
        }

    override suspend fun clearAuth(): Unit =
        withContext(dispatcher) {
            queries.clearLocalAuth()
        }
}
