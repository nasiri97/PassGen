package ir.ornix.passgen.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import ir.ornix.passgen.core.domain.MasterKeyRepository

class SettingsMasterKeyRepository(private val settings: Settings = Settings()) : MasterKeyRepository {
    private val KEY = "master_key"

    override fun saveMasterKey(key: String) {
        settings.putString(KEY, key)
    }

    override fun hasMasterKey(): Boolean {
        return settings.hasKey(KEY)
    }

    override fun getMasterKey(): String? {
        return settings.getStringOrNull(KEY)
    }
}
