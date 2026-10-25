package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository
import kotlinx.coroutines.flow.Flow

class IsBiometricEnabledUseCase(private val repository: LocalAuthRepository) {
    operator fun invoke(): Flow<Boolean> = repository.isBiometricEnabled()
}
