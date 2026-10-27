package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakePassGenConfigRepository : PassGenConfigRepository {
    private val configs = MutableStateFlow<List<PassGenConfig>>(emptyList())

    override suspend fun add(config: PassGenConfig): Int {
        configs.update { it + config }
        return config.id
    }

    override suspend fun removeById(configId: Int) {
        configs.update { it.filterNot { config -> config.id == configId } }
    }

    override fun getAll(): Flow<List<PassGenConfig>> = configs
}
