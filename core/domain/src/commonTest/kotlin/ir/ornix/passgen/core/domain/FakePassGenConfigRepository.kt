package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakePassGenConfigRepository : PassGenConfigRepository {
    private val configs = MutableStateFlow<List<PassGenConfig>>(emptyList())

    override suspend fun add(config: PassGenConfig): Int {
        configs.update { it + config }
        return config.id
    }

    override suspend fun removeKdfPassGenConfigById(configId: Int) {
        configs.update { it.filterNot { config -> config.id == configId } }
    }

    override suspend fun removeRandomPassGenConfigById(configId: Int) {
        configs.update { it.filterNot { config -> config.id == configId } }
    }

    override suspend fun getAllKdfPassGenConfigIds(): List<Int> {
        return configs.value.filterIsInstance<KdfPassGenConfig>().map { it.id }
    }

    override fun getAllKdfPassGenConfigs(): Flow<List<KdfPassGenConfig>> =
        configs.map { list ->
            list.filterIsInstance<KdfPassGenConfig>()
        }

    override fun getAllRandomPassGenConfigs(): Flow<List<RandomPassGenConfig>> =
        configs.map { list ->
            list.filterIsInstance<RandomPassGenConfig>()
        }

    override suspend fun deleteAllPassGenConfigs() {
        configs.value = emptyList()
    }
}
