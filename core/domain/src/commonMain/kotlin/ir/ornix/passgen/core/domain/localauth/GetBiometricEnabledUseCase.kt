package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository

class GetBiometricEnabledUseCase(private val repository: LocalAuthRepository) {
    operator fun invoke(): Boolean = repository.isBiometricEnabled()
}
