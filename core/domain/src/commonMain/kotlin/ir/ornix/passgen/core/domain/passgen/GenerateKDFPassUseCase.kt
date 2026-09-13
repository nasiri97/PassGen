package ir.ornix.passgen.core.domain.passgen

import ir.ornix.passgen.core.domain.passgen.model.PassGenFeed
import ir.ornix.passgen.core.domain.passgen.model.PassGenWrapper
import ir.ornix.passgen.core.domain.masterkey.RetrieveMasterKeyUseCase
import ir.ornix.passgen.core.domain.passgenconfig.GetAllPassGenConfigsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GenerateKDFPassUseCase(
    private val getAllPassGenConfigs: GetAllPassGenConfigsUseCase,
    private val retrieveMasterKeyUseCase: RetrieveMasterKeyUseCase,
) {

    private val masterKey = retrieveMasterKeyUseCase()

    private var currentPassGenWrappers = mutableListOf<PassGenWrapper>()

    operator fun invoke(
        input: StateFlow<String>
    ): Flow<List<PassGenWrapper>> {

        val feed = combine(masterKey, input) { mKey, inp ->
            mKey?.let { PassGenFeed(masterKey = it, input = inp) }
        }

        val kdfPassGenConfigs = getAllPassGenConfigs()

        val passGenWrappersFlow: Flow<List<PassGenWrapper>> = kdfPassGenConfigs.map { configs ->
            val list = mutableListOf<PassGenWrapper>()
            configs.forEach { config ->
                list.add(
                    currentPassGenWrappers.find { it.passGenConfig.id == config.id }
                        ?: PassGenWrapper(config, feed)
                )
            }
            currentPassGenWrappers = list
            list
        }

        return passGenWrappersFlow
    }
}