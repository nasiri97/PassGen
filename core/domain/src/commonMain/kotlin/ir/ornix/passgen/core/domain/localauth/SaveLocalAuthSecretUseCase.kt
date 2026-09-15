package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.LocalAuthType

class SaveLocalAuthSecretUseCase(private val repository: LocalAuthRepository) {
    operator fun invoke(type: LocalAuthType, secret: String) {
        repository.setLocalAuthType(type)
        repository.saveSecretHash(secret)
    }
}
