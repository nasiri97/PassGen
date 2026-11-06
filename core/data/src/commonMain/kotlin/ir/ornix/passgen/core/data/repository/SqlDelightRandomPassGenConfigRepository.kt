package ir.ornix.passgen.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.database.RandomPassGenConfigEntity
import ir.ornix.passgen.core.domain.RandomPassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightRandomPassGenConfigRepository(
    private val database: PassGenDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : RandomPassGenConfigRepository {

    private val queries = database.randomPassGenConfigQueries

    override suspend fun getConfigById(configId: Long): RandomPassGenConfig? =
        withContext(dispatcher) {
            queries.selectConfigById(configId).awaitAsOneOrNull()?.toRandomPassGenConfig()
        }

    override suspend fun addConfig(config: RandomPassGenConfig): Long =
        withContext(dispatcher) {
            database.transactionWithResult {
                val configId: Long = if (config.id > 0) {
                    queries.insertConfigWithId(
                        id = config.id,
                        name = config.name,
                        password_length = config.passwordLength?.toLong(),
                        pass_encoder_key = config.passEncoder.key
                    )
                    config.id
                } else {
                    queries.insertConfigAutoId(
                        name = config.name,
                        password_length = config.passwordLength?.toLong(),
                        pass_encoder_key = config.passEncoder.key
                    )
                    queries.lastInsertedId().awaitAsOne()
                }

                configId
            }
        }

    override suspend fun removeConfigById(configId: Long): Unit =
        withContext(dispatcher) {
            queries.deleteConfigById(configId)
        }

    override fun getAllConfigs(): Flow<List<RandomPassGenConfig>> {
        return queries.selectAllConfigs()
            .asFlow()
            .mapToList(dispatcher)
            .map { list ->
                list.map { row -> row.toRandomPassGenConfig() }
            }
    }

    override suspend fun deleteAllConfigs(): Unit =
        withContext(dispatcher) {
            queries.deleteAllConfigs()
        }
}

private fun RandomPassGenConfigEntity.toRandomPassGenConfig(): RandomPassGenConfig {
    return RandomPassGenConfig(
        id = this.id,
        name = this.name,
        passEncoder = PassEncoder.fromKey(this.pass_encoder_key),
        passwordLength = this.password_length?.toInt()
    )
}