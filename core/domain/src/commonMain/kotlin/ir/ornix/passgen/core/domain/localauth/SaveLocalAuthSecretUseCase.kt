package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.Crypto
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.model.localauth.LocalAuthType

class SaveLocalAuthSecretUseCase(
    private val crypto: Crypto,
    private val repository: LocalAuthRepository
) {
    suspend operator fun invoke(type: LocalAuthType, secret: ByteArray) {
        repository.setLocalAuthType(type)
        repository.saveEncryptedSecret(crypto.encrypt(secret))
        repository.resetFailedAuthAttemptCount()
        secret.fill(0)
    }
}
