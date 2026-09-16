package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.LocalAuthType
import kotlinx.coroutines.flow.StateFlow

class GetLocalAuthTypeUseCase(private val repository: LocalAuthRepository) {
    operator fun invoke(): StateFlow<LocalAuthType> = repository.getLocalAuthType()
}
