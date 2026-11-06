package ir.ornix.passgen.core.domain.passgenconfig.random

import ir.ornix.passgen.core.domain.RandomPassGenConfigRepository

class RemoveRandomPassGenConfigUseCase(
    private val repo: RandomPassGenConfigRepository
) {
    suspend operator fun invoke(configId: Long) {
        repo.removeConfigById(configId)
    }
}