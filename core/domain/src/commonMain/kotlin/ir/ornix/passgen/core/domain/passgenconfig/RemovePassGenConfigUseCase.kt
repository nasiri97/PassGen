package ir.ornix.passgen.core.domain.passgenconfig

import ir.ornix.passgen.core.domain.PassGenConfigRepository

class RemovePassGenConfigUseCase(
    private val repo: PassGenConfigRepository
) {
    suspend operator fun invoke(configId: Int) = repo.removeById(configId)
}