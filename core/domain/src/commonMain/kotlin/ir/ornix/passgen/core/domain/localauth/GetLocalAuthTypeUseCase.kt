package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.LocalAuthType

class GetLocalAuthTypeUseCase(private val repository: LocalAuthRepository) {
    operator fun invoke(): LocalAuthType = repository.getLocalAuthType()
}
