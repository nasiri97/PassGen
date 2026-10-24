package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.Credential
import kotlinx.coroutines.flow.Flow

interface CredentialRepository {
    suspend fun save(credential: Credential)
    suspend fun delete(credentialId: Long)
    fun getAll(): Flow<List<Credential>>
}
