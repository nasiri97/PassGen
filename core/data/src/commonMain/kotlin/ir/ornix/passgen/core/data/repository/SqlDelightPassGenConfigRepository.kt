package ir.ornix.passgen.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightPassGenConfigRepository(
    private val database: PassGenDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : PassGenConfigRepository {

    private val kdfQueries = database.kdfPassGenConfigQueries
    private val randomQueries = database.randomPassGenConfigQueries

    override suspend fun add(config: PassGenConfig): Int =
        withContext(dispatcher) {
            when (config) {
                is RandomPassGenConfig -> addRandomPassGenConfig(config)
                is KdfPassGenConfig -> addKdfPassGenConfig(config)
            }
        }

    private suspend fun addRandomPassGenConfig(config: PassGenConfig): Int =
        withContext(dispatcher) {
            database.transactionWithResult {
                val configId: Long = if (config.id > 0) {
                    randomQueries.insertRandomPassGenConfigWithId(
                        id = config.id.toLong(),
                        name = config.name,
                        password_length = config.passwordLength?.toLong(),
                        pass_encoder_key = config.passEncoder.key
                    )
                    config.id.toLong()
                } else {
                    randomQueries.insertRandomPassGenConfigAutoId(
                        name = config.name,
                        password_length = config.passwordLength?.toLong(),
                        pass_encoder_key = config.passEncoder.key
                    )
                    randomQueries.lastInsertedId().awaitAsOne()
                }

                configId.toInt()
            }
        }

    private suspend fun addKdfPassGenConfig(config: KdfPassGenConfig): Int =
        withContext(dispatcher) {
            database.transactionWithResult {
                val configId: Long = if (config.id > 0) {
                    kdfQueries.insertKdfPassGenConfigWithId(
                        id = config.id.toLong(),
                        name = config.name,
                        password_length = config.passwordLength?.toLong(),
                        pass_encoder_key = config.passEncoder.key,
                        input_hasher_key = config.inputHasher.key,
                        trim_spaces = if (config.preprocessConfig.trimLeadingAndTrailingSpaces) 1L else 0L,
                        collapse_spaces = if (config.preprocessConfig.collapseMultipleSpaces) 1L else 0L,
                        convert_lowercase = if (config.preprocessConfig.convertToLowercase) 1L else 0L
                    )
                    config.id.toLong()
                } else {
                    kdfQueries.insertKdfPassGenConfigAutoId(
                        name = config.name,
                        password_length = config.passwordLength?.toLong(),
                        pass_encoder_key = config.passEncoder.key,
                        input_hasher_key = config.inputHasher.key,
                        trim_spaces = if (config.preprocessConfig.trimLeadingAndTrailingSpaces) 1L else 0L,
                        collapse_spaces = if (config.preprocessConfig.collapseMultipleSpaces) 1L else 0L,
                        convert_lowercase = if (config.preprocessConfig.convertToLowercase) 1L else 0L
                    )
                    kdfQueries.lastInsertedId().awaitAsOne()
                }

                configId.toInt()
            }
        }

    override suspend fun removeKdfPassGenConfigById(configId: Int): Unit =
        withContext(dispatcher) {
            kdfQueries.deleteKdfPassGenConfigById(configId.toLong())
        }

    override suspend fun removeRandomPassGenConfigById(configId: Int): Unit =
        withContext(dispatcher) {
            randomQueries.deleteRandomPassGenConfigById(configId.toLong())
        }

    override suspend fun getAllKdfPassGenConfigIds(): List<Int> =
        withContext(dispatcher) {
            kdfQueries.selectAllKdfPassGenConfigIds()
                .awaitAsList()
                .map(Long::toInt)
        }

    override fun getAllKdfPassGenConfigs(): Flow<List<KdfPassGenConfig>> {
        return kdfQueries.selectAllKdfPassGenConfigs()
            .asFlow()
            .mapToList(dispatcher)
            .map { list ->
                list.map { row ->
                    KdfPassGenConfig(
                        id = row.id.toInt(),
                        name = row.name,
                        preprocessConfig = PreprocessConfig(
                            trimLeadingAndTrailingSpaces = (row.trim_spaces) == 1L,
                            collapseMultipleSpaces = (row.collapse_spaces) == 1L,
                            convertToLowercase = (row.convert_lowercase) == 1L
                        ),
                        inputHasher = InputHasher.fromKey(row.input_hasher_key),
                        passEncoder = PassEncoder.fromKey(row.pass_encoder_key),
                        passwordLength = row.password_length?.toInt()
                    )
                }
            }
    }

    override fun getAllRandomPassGenConfigs(): Flow<List<RandomPassGenConfig>> {
        return randomQueries.selectAllRandomPassGenConfigs()
            .asFlow()
            .mapToList(dispatcher)
            .map { list ->
                list.map { row ->
                    RandomPassGenConfig(
                        id = row.id.toInt(),
                        name = row.name,
                        passEncoder = PassEncoder.fromKey(row.pass_encoder_key),
                        passwordLength = row.password_length?.toInt()
                    )
                }
            }
    }

    override suspend fun deleteAllPassGenConfigs() =
        withContext(dispatcher) {
            database.transaction {
                kdfQueries.deleteAllKdfPassGenConfigs()
                randomQueries.deleteAllRandomPassGenConfigs()
            }
        }
}
