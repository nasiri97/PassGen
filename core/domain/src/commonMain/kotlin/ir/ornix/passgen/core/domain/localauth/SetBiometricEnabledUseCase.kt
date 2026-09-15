package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository

class SetBiometricEnabledUseCase(private val repository: LocalAuthRepository) {
    operator fun invoke(enabled: Boolean) {
        repository.setBiometricEnabled(enabled)
    }
}
