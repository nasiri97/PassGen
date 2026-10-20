package ir.ornix.passgen.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
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
    private val driver: SqlDriver,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : PassGenConfigRepository {

    private val queries = database.passGenConfigQueries

    override suspend fun add(config: PassGenConfig): Int = withContext(ioDispatcher) {
        try {
            PassGenDatabase.Schema.create(driver).await()
        } catch (_: Exception) {
        }

        database.transactionWithResult {
            val configId: Long = if (config.id > 0) {
                queries.insertPassGenConfigWithId(
                    id = config.id.toLong(),
                    name = config.name,
                    type = when (config) {
                        is KdfPassGenConfig -> TYPE_KDF
                        is RandomPassGenConfig -> TYPE_RANDOM
                    },
                    pass_encoder_key = config.passEncoder.key,
                    password_length = config.passwordLength?.toLong()
                )
                config.id.toLong()
            } else {
                queries.insertPassGenConfigAutoId(
                    name = config.name,
                    type = when (config) {
                        is KdfPassGenConfig -> TYPE_KDF
                        is RandomPassGenConfig -> TYPE_RANDOM
                    },
                    pass_encoder_key = config.passEncoder.key,
                    password_length = config.passwordLength?.toLong()
                )
                queries.lastInsertedId().awaitAsOne()
            }

            if (config is KdfPassGenConfig) {
                queries.insertKdfDetails(
                    config_id = configId,
                    input_hasher_key = config.inputHasher.key,
                    trim_spaces = if (config.preprocessConfig.trimLeadingAndTrailingSpaces) 1L else 0L,
                    collapse_spaces = if (config.preprocessConfig.collapseMultipleSpaces) 1L else 0L,
                    convert_lowercase = if (config.preprocessConfig.convertToLowercase) 1L else 0L
                )
            }

            configId.toInt()
        }
    }

    override suspend fun removeById(configId: Int): Unit = withContext(ioDispatcher) {
        try {
            PassGenDatabase.Schema.create(driver).await()
        } catch (_: Exception) {
        }
        queries.deletePassGenConfigById(configId.toLong())
    }

    override fun getAll(): Flow<List<PassGenConfig>> {
        return queries.selectAllPassGenConfigs()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { list ->
                list.mapNotNull { row ->
                    try {
                        val passEncoder = PassEncoder.fromKey(row.pass_encoder_key)
                        val passwordLength = row.password_length?.toInt()
                        val id = row.id.toInt()

                        when (row.type) {
                            TYPE_KDF -> {
                                val inputHasherKey = row.input_hasher_key ?: return@mapNotNull null
                                val inputHasher = InputHasher.fromKey(inputHasherKey)
                                val preprocessConfig = PreprocessConfig(
                                    trimLeadingAndTrailingSpaces = (row.trim_spaces ?: 1L) == 1L,
                                    collapseMultipleSpaces = (row.collapse_spaces ?: 1L) == 1L,
                                    convertToLowercase = (row.convert_lowercase ?: 1L) == 1L
                                )
                                KdfPassGenConfig(
                                    id = id,
                                    name = row.name,
                                    preprocessConfig = preprocessConfig,
                                    inputHasher = inputHasher,
                                    passEncoder = passEncoder,
                                    passwordLength = passwordLength
                                )
                            }

                            TYPE_RANDOM -> {
                                RandomPassGenConfig(
                                    id = id,
                                    name = row.name,
                                    passEncoder = passEncoder,
                                    passwordLength = passwordLength
                                )
                            }

                            else -> null
                        }
                    } catch (_: Exception) {
                        null
                    }
                }
            }
    }

    companion object {
        private const val TYPE_KDF = "KDF"
        private const val TYPE_RANDOM = "RANDOM"
    }
}
