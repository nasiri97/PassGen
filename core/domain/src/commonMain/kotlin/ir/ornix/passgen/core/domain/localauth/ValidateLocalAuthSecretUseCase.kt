package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.Crypto
import ir.ornix.passgen.core.domain.LocalAuthRepository

class ValidateLocalAuthSecretUseCase(
    private val crypto: Crypto,
    private val repository: LocalAuthRepository
) {
    operator fun invoke(secret: ByteArray): Boolean {
        val realSecret = crypto.decrypt(repository.getEncryptedSecret())
        val isValid = realSecret.contentEquals(secret)
        secret.fill(0)
        realSecret.fill(0)
        return isValid
    }
}
