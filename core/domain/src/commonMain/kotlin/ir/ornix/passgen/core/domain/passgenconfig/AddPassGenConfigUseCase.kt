package ir.ornix.passgen.core.domain.passgenconfig

import ir.ornix.passgen.core.domain.core.KDFPassGenConfig
import ir.ornix.passgen.core.domain.PassGenConfigRepository


class AddPassGenConfigUseCase(
    private val repo: PassGenConfigRepository
) {
    suspend operator fun invoke(config: KDFPassGenConfig) = repo.add(config)
}