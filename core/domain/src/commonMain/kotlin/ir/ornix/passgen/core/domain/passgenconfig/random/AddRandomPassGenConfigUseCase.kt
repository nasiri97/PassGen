package ir.ornix.passgen.core.domain.passgenconfig.random

import ir.ornix.passgen.core.domain.RandomPassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig

class AddRandomPassGenConfigUseCase(
    private val repo: RandomPassGenConfigRepository
) {
    suspend operator fun invoke(config: RandomPassGenConfig) {
        repo.addConfig(config)
    }
}