package ir.ornix.passgen.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class SettingsPassGenConfigRepository(private val settings: Settings) : PassGenConfigRepository {

    companion object {
        private const val KEY = "pass_gen_configs"
    }

    private val configsStateFlow = MutableStateFlow<List<KDFPassGenConfig>>(run {
        val storedValue = settings.getStringOrNull(KEY)
        if (storedValue != null) {
            try {
                val decryptedJson = PlatformCrypto.decrypt(storedValue)
                Json.decodeFromString(decryptedJson)
            } catch (e: Exception) {
                emptyList()
            }
        } else emptyList()
    })

    override suspend fun add(config: KDFPassGenConfig): Int {
        val current = configsStateFlow.value.toMutableList()
        val newId = (current.maxOfOrNull { it.id } ?: -1) + 1
        current.add(config.copy(id = newId))
        save(current)
        return newId
    }

    override suspend fun removeById(configId: Int) {
        val current = configsStateFlow.value.filter { it.id != configId }
        save(current)
    }

    override fun getAll(): Flow<List<KDFPassGenConfig>> = configsStateFlow.asStateFlow()

    private fun save(configs: List<KDFPassGenConfig>) {
        val json = Json.encodeToString(configs)
        val encryptedJson = PlatformCrypto.encrypt(json)
        settings[KEY] = encryptedJson
        configsStateFlow.value = configs
    }
}
