package ir.ornix.passgen.core.domain.passgenconfig

import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.passwordgenerator.kdf.KDFPassGenConfig


class AddPassGenConfigUseCase(
    private val repo: PassGenConfigRepository
) {
    suspend operator fun invoke(config: KDFPassGenConfig) = repo.add(config)
}