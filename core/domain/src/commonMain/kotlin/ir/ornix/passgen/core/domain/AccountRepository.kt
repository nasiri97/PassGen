package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun save(account: Account)
    suspend fun delete(accountId: Int)
    fun getAll(): Flow<List<Account>>
}
