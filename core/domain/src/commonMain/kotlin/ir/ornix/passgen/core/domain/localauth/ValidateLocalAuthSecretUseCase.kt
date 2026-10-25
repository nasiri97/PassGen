package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.Crypto
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.logging.Logger

class ValidateLocalAuthSecretUseCase(
    private val crypto: Crypto,
    private val repository: LocalAuthRepository
) {
    suspend operator fun invoke(secret: ByteArray): Boolean {
        val encryptedSecret = repository.getEncryptedSecret() ?: run {
            Logger.e("No encrypted secret found")
            return false
        }
        val realSecret = crypto.decrypt(encryptedSecret)
        val isValid = realSecret.contentEquals(secret)
        secret.fill(0)
        realSecret.fill(0)
        return isValid
    }
}
