package ir.ornix.passgen.core.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.KDFPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class SettingsPassGenConfigRepository(private val settings: Settings) : PassGenConfigRepository {

    companion object {
        private const val KEY = "pass_gen_configs"
    }

    private val configsStateFlow = MutableStateFlow<List<PassGenConfig>>(run {
        val storedValue = settings.getStringOrNull(KEY)
        if (storedValue != null) {
            try {
                Json.decodeFromString(storedValue)
            } catch (e: Exception) {
                emptyList()
            }
        } else emptyList()
    })

    override suspend fun add(config: PassGenConfig): Int {
        val current = configsStateFlow.value.toMutableList()
        val newId = (current.maxOfOrNull { it.id } ?: -1) + 1

        val newConfig = when (config) {
            is KDFPassGenConfig -> config.copy(id = newId)
            is RandomPassGenConfig -> config.copy(id = newId)
        }

        current.add(newConfig)
        save(current)
        return newId
    }

    override suspend fun removeById(configId: Int) {
        val current = configsStateFlow.value.filter { it.id != configId }
        save(current)
    }

    override fun getAll(): Flow<List<PassGenConfig>> = configsStateFlow.asStateFlow()

    private fun save(configs: List<PassGenConfig>) {
        val json = Json.encodeToString(configs)
        settings[KEY] = json
        configsStateFlow.value = configs
    }
}
