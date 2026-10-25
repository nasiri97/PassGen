package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.LocalAuthType
import kotlinx.coroutines.flow.Flow

class GetLocalAuthTypeUseCase(private val repository: LocalAuthRepository) {
    operator fun invoke(): Flow<LocalAuthType> = repository.getLocalAuthType()
}
