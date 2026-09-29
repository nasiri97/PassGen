package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.passgenconfig.KDFPassGenConfig
import kotlinx.coroutines.flow.Flow

interface PassGenConfigRepository {
    suspend fun add(config: KDFPassGenConfig): Int
    suspend fun removeById(configId: Int)
    fun getAll(): Flow<List<KDFPassGenConfig>>
}