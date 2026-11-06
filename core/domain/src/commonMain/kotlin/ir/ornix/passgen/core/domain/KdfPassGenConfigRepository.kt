package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import kotlinx.coroutines.flow.Flow

interface KdfPassGenConfigRepository {
    suspend fun getConfigById(configId: Long): KdfPassGenConfig?
    suspend fun addConfig(config: KdfPassGenConfig): Long
    suspend fun removeConfigById(configId: Long)
    fun getAllConfigs(): Flow<List<KdfPassGenConfig>>
    suspend fun deleteAllConfigs()
    suspend fun doesMasterKeyDigestIdExist(masterKeyDigestId: String): Boolean
    suspend fun getAllMasterKeyDigestIds(): List<String>
}