package ir.ornix.passgen.core.data

import com.russhwolf.settings.Settings
import ir.ornix.passgen.core.domain.MasterKeyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsMasterKeyRepository(private val settings: Settings = Settings()) : MasterKeyRepository {
    private val KEY = "master_key"
    
    private val _masterKey = MutableStateFlow<String?>(settings.getStringOrNull(KEY))

    override suspend fun saveMasterKey(key: String) {
        settings.putString(KEY, key)
        _masterKey.value = key
    }

    override fun getMasterKey(): Flow<String?> = _masterKey.asStateFlow()

    override suspend fun clearMasterKey() {
        settings.remove(KEY)
        _masterKey.value = null
    }
}
