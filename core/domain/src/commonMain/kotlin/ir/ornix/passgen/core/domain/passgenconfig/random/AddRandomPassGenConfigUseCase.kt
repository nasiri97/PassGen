package ir.ornix.passgen.core.domain.passgenconfig.random

import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig

class AddRandomPassGenConfigUseCase(
    private val passGenConfigRepo: PassGenConfigRepository
) {
    suspend operator fun invoke(config: RandomPassGenConfig) {
        passGenConfigRepo.add(config)
    }
}