package ir.ornix.passgen.core.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.AccountRepository
import ir.ornix.passgen.core.model.Account
import ir.ornix.passgen.core.model.Password
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightAccountRepository(
    database: PassGenDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : AccountRepository {

    private val queries = database.accountQueries

    override suspend fun save(account: Account): Unit = withContext(ioDispatcher) {
        if (account.id > 0) {
            queries.insertAccount(
                id = account.id.toLong(),
                username = account.username,
                password_value = account.password.value.concatToString(),
                entropy_byte_size = account.password.entropyByteSize.toLong()
            )
        } else {
            queries.insertAccountAutoId(
                username = account.username,
                password_value = account.password.value.concatToString(),
                entropy_byte_size = account.password.entropyByteSize.toLong()
            )
        }
    }

    override suspend fun delete(accountId: Int): Unit = withContext(ioDispatcher) {
        queries.deleteAccountById(accountId.toLong())
    }

    override fun getAll(): Flow<List<Account>> {
        return queries.selectAllAccounts()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { list ->
                list.map { entity ->
                    Account(
                        id = entity.id.toInt(),
                        username = entity.username,
                        password = Password(
                            value = entity.password_value.toCharArray(),
                            entropyByteSize = entity.entropy_byte_size.toInt()
                        )
                    )
                }
            }
    }
}
