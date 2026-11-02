package ir.ornix.passgen.core.domain.passgenconfig.kdf

import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import kotlinx.coroutines.flow.Flow

class GetAllKdfPassGenConfigsUseCase(
    private val repo: PassGenConfigRepository
) {
    operator fun invoke(): Flow<List<KdfPassGenConfig>> = repo.getAllKdfPassGenConfigs()
}