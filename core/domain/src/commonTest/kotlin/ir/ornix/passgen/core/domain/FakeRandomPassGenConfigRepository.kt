package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeRandomPassGenConfigRepository : RandomPassGenConfigRepository {

    private val configs = MutableStateFlow<List<RandomPassGenConfig>>(emptyList())

    override suspend fun getConfigById(configId: Long): RandomPassGenConfig? {
        return configs.value.find { it.id == configId }
    }

    override suspend fun addConfig(config: RandomPassGenConfig): Long {
        configs.update { it + config }
        return config.id
    }

    override suspend fun removeConfigById(configId: Long) {
        configs.update { it.filterNot { config -> config.id == configId } }
    }

    override fun getAllConfigs(): Flow<List<RandomPassGenConfig>> = configs

    override suspend fun deleteAllConfigs() {
        configs.value = emptyList()
    }
}