package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository
import kotlinx.coroutines.flow.StateFlow

class IsBiometricEnabledUseCase(private val repository: LocalAuthRepository) {
    operator fun invoke(): StateFlow<Boolean> = repository.isBiometricEnabled()
}
