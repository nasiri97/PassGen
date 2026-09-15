package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository

class ValidateLocalAuthSecretUseCase(private val repository: LocalAuthRepository) {
    operator fun invoke(secret: String): Boolean = repository.validateSecretHash(secret)
}
