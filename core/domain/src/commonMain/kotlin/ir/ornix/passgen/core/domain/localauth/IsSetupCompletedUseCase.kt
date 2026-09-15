package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository

class IsSetupCompletedUseCase(private val repository: LocalAuthRepository) {
    operator fun invoke(): Boolean = repository.isSetupCompleted()
}
