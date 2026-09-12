package ir.ornix.passgen.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.domain.core.KDFPassGenConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class SettingsPassGenConfigRepository(private val settings: Settings) : PassGenConfigRepository {
    private val KEY = "pass_gen_configs"
    private val _configs = MutableStateFlow<List<KDFPassGenConfig>>(emptyList())

    init {
        loadConfigs()
    }

    private fun loadConfigs() {
        val json = settings.getStringOrNull(KEY)
        if (json != null) {
            try {
                _configs.value = Json.decodeFromString(json)
            } catch (e: Exception) {
                _configs.value = emptyList()
            }
        }
    }

    override suspend fun add(config: KDFPassGenConfig) {
        val current = _configs.value.toMutableList()
        val newId = (current.maxOfOrNull { it.id } ?: -1) + 1
        current.add(config.copy(id = newId))
        save(current)
    }

    override suspend fun removeById(configId: Int) {
        val current = _configs.value.filter { it.id != configId }
        save(current)
    }

    override fun getAll(): Flow<List<KDFPassGenConfig>> = _configs.asStateFlow()

    private fun save(configs: List<KDFPassGenConfig>) {
        val json = Json.encodeToString(configs)
        settings[KEY] = json
        _configs.value = configs
    }
}
