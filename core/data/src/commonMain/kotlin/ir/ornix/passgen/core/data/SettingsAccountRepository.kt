package ir.ornix.passgen.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import ir.ornix.passgen.core.domain.AccountRepository
import ir.ornix.passgen.core.domain.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingsAccountRepository(private val settings: Settings) : AccountRepository {
    private val KEY = "saved_accounts"
    private val _accounts = MutableStateFlow<List<Account>>(emptyList())

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        val json = settings.getStringOrNull(KEY)
        if (json != null) {
            try {
                _accounts.value = Json.decodeFromString(json)
            } catch (e: Exception) {
                _accounts.value = emptyList()
            }
        }
    }

    override suspend fun save(account: Account) {
        val current = _accounts.value.toMutableList()
        val newId = (current.maxOfOrNull { it.id } ?: -1) + 1
        current.add(account.copy(id = newId))
        persist(current)
    }

    override suspend fun delete(accountId: Int) {
        val current = _accounts.value.filter { it.id != accountId }
        persist(current)
    }

    override fun getAll(): Flow<List<Account>> = _accounts.asStateFlow()

    private fun persist(accounts: List<Account>) {
        val json = Json.encodeToString(accounts)
        settings[KEY] = json
        _accounts.value = accounts
    }
}
