package ir.ornix.passgen.core.domain

import ir.ornix.passgen.passwordgenerator.kdf.KDFPassGenConfig
import kotlinx.coroutines.flow.Flow

interface PassGenConfigRepository {
    suspend fun add(config: KDFPassGenConfig)
    suspend fun removeById(configId: Int)
    fun getAll(): Flow<List<KDFPassGenConfig>>
}