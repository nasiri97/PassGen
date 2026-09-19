package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakePassGenConfigRepository : PassGenConfigRepository {
    private val configs = MutableStateFlow<List<KDFPassGenConfig>>(emptyList())

    override suspend fun add(config: KDFPassGenConfig): Int {
        configs.update { it + config }
        return config.id
    }

    override suspend fun removeById(configId: Int) {
        configs.update { it.filterNot { it.id == configId } }
    }

    override fun getAll(): Flow<List<KDFPassGenConfig>> = configs
}
