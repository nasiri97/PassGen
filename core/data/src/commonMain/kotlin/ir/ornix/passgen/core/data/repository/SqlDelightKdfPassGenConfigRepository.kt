package ir.ornix.passgen.core.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.database.KdfPassGenConfigEntity
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.KdfPassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightKdfPassGenConfigRepository(
    private val database: PassGenDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : KdfPassGenConfigRepository {

    private val queries = database.kdfPassGenConfigQueries

    override suspend fun getConfigById(configId: Long): KdfPassGenConfig? =
        withContext(dispatcher) {
            queries.selectConfigById(configId).awaitAsOneOrNull()?.toKdfPassGenConfig()
        }

    override suspend fun addConfig(config: KdfPassGenConfig): Long =
        withContext(dispatcher) {
            database.transactionWithResult {
                val configId: Long = if (config.id > 0) {
                    queries.insertConfigWithId(
                        id = config.id,
                        name = config.name,
                        password_length = config.passwordLength?.toLong(),
                        pass_encoder_key = config.passEncoder.key,
                        input_hasher_key = config.inputHasher.key,
                        master_key_digest_id = config.masterKeyDigestId,
                        trim_spaces = if (config.preprocessConfig.trimLeadingAndTrailingSpaces) 1L else 0L,
                        collapse_spaces = if (config.preprocessConfig.collapseMultipleSpaces) 1L else 0L,
                        convert_lowercase = if (config.preprocessConfig.convertToLowercase) 1L else 0L
                    )
                    config.id
                } else {
                    queries.insertConfigAutoId(
                        name = config.name,
                        password_length = config.passwordLength?.toLong(),
                        pass_encoder_key = config.passEncoder.key,
                        input_hasher_key = config.inputHasher.key,
                        master_key_digest_id = config.masterKeyDigestId,
                        trim_spaces = if (config.preprocessConfig.trimLeadingAndTrailingSpaces) 1L else 0L,
                        collapse_spaces = if (config.preprocessConfig.collapseMultipleSpaces) 1L else 0L,
                        convert_lowercase = if (config.preprocessConfig.convertToLowercase) 1L else 0L
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

    override fun getAllConfigs(): Flow<List<KdfPassGenConfig>> {
        return queries.selectAllConfigs()
            .asFlow()
            .mapToList(dispatcher)
            .map { list ->
                list.map { row -> row.toKdfPassGenConfig() }
            }
    }

    override suspend fun deleteAllConfigs(): Unit =
        withContext(dispatcher) {
            queries.deleteAllConfigs()
        }

    override suspend fun doesMasterKeyDigestIdExist(masterKeyDigestId: String): Boolean =
        withContext(dispatcher) {
            queries.doesMasterKeyDigestIdExist(masterKeyDigestId).awaitAsOne()
        }

    override suspend fun getAllMasterKeyDigestIds(): List<String> =
        withContext(dispatcher) {
            queries.selectAllMasterKeyDigestIds().awaitAsList()
        }
}

private fun KdfPassGenConfigEntity.toKdfPassGenConfig(): KdfPassGenConfig {
    return KdfPassGenConfig(
        id = this.id,
        name = this.name,
        masterKeyDigestId = this.master_key_digest_id,
        preprocessConfig = PreprocessConfig(
            trimLeadingAndTrailingSpaces = (this.trim_spaces) == 1L,
            collapseMultipleSpaces = (this.collapse_spaces) == 1L,
            convertToLowercase = (this.convert_lowercase) == 1L
        ),
        inputHasher = InputHasher.fromKey(this.input_hasher_key),
        passEncoder = PassEncoder.fromKey(this.pass_encoder_key),
        passwordLength = this.password_length?.toInt()
    )
}