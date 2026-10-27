package ir.ornix.passgen.core.domain.passgenconfig.random

import ir.ornix.passgen.core.domain.PassGenConfigRepository

class RemoveRandomPassGenConfigUseCase(
    private val configRepo: PassGenConfigRepository
) {
    suspend operator fun invoke(configId: Int) {
        configRepo.removeById(configId)
    }
}