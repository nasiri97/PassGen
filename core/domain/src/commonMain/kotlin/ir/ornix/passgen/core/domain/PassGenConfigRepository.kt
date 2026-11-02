package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.flow.Flow

interface PassGenConfigRepository {
    suspend fun add(config: PassGenConfig): Int
    suspend fun removeKdfPassGenConfigById(configId: Int)
    suspend fun removeRandomPassGenConfigById(configId: Int)
    suspend fun getAllKdfPassGenConfigIds(): List<Int>
    fun getAllKdfPassGenConfigs(): Flow<List<KdfPassGenConfig>>
    fun getAllRandomPassGenConfigs(): Flow<List<RandomPassGenConfig>>
    suspend fun deleteAllPassGenConfigs()
}