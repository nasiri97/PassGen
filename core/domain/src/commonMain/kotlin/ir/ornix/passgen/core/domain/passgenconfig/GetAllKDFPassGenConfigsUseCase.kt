package ir.ornix.passgen.core.domain.passgenconfig

import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.KDFPassGenConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllKDFPassGenConfigsUseCase(
    private val repo: PassGenConfigRepository
) {
    operator fun invoke(): Flow<List<KDFPassGenConfig>> =
        repo.getAll().map { configs ->
            configs.filterIsInstance<KDFPassGenConfig>()
        }
}