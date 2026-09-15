package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository

class SetSetupCompletedUseCase(private val repository: LocalAuthRepository) {
    operator fun invoke(completed: Boolean) = repository.setSetupCompleted(completed)
}
