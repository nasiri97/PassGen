package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeKdfPassGenConfigRepository : KdfPassGenConfigRepository {

    private val configs = MutableStateFlow<List<KdfPassGenConfig>>(emptyList())

    override suspend fun getConfigById(configId: Long): KdfPassGenConfig? {
        return configs.value.find { it.id == configId }
    }

    override suspend fun addConfig(config: KdfPassGenConfig): Long {
        configs.update { it + config }
        return config.id
    }

    override suspend fun removeConfigById(configId: Long) {
        configs.update { it.filterNot { config -> config.id == configId } }
    }

    override fun getAllConfigs(): Flow<List<KdfPassGenConfig>> = configs

    override suspend fun deleteAllConfigs() {
        configs.value = emptyList()
    }

    override suspend fun getAllMasterKeyDigestIds(): List<String> {
        return configs.value.map { it.masterKeyDigestId }
    }

    override suspend fun doesMasterKeyDigestIdExist(masterKeyDigestId: String): Boolean {
        return configs.value.map { it.masterKeyDigestId }.contains(masterKeyDigestId)
    }
}