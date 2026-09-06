package ir.ornix.passgen.core.domain.passgenconfig

import ir.ornix.passgen.core.domain.PassGenConfigRepository

class GetAllPassGenConfigsUseCase(
    private val repo: PassGenConfigRepository
) {
    operator fun invoke() = repo.getAll()
}