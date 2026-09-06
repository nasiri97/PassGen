package ir.ornix.passgen.core.domain

import kotlinx.coroutines.flow.Flow

interface MasterKeyRepository {
    suspend fun saveMasterKey(key: String)
    fun getMasterKey(): Flow<String?>
    suspend fun clearMasterKey()
}
