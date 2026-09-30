package ir.ornix.passgen.core.domain.passgenconfig

import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllRandomPassGenConfigsUseCase(
    private val repo: PassGenConfigRepository
) {
    operator fun invoke(): Flow<List<RandomPassGenConfig>> =
        repo.getAll().map { configs ->
            configs.filterIsInstance<RandomPassGenConfig>()
        }
}