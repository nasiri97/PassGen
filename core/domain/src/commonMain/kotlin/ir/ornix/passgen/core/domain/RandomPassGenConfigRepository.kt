package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.flow.Flow

interface RandomPassGenConfigRepository {
    suspend fun getConfigById(configId: Long): RandomPassGenConfig?
    suspend fun addConfig(config: RandomPassGenConfig): Long
    suspend fun removeConfigById(configId: Long)
    fun getAllConfigs(): Flow<List<RandomPassGenConfig>>
    suspend fun deleteAllConfigs()
}