package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import kotlinx.coroutines.flow.Flow

interface PassGenConfigRepository {
    suspend fun add(config: PassGenConfig): Int
    suspend fun removeById(configId: Int)
    fun getAll(): Flow<List<PassGenConfig>>
}