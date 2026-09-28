package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.Crypto
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.LocalAuthType

class SaveLocalAuthSecretUseCase(
    private val crypto: Crypto,
    private val repository: LocalAuthRepository
) {
    operator fun invoke(type: LocalAuthType, secret: ByteArray) {
        repository.setLocalAuthType(type)
        repository.saveEncryptedSecret(crypto.encrypt(secret))
        secret.fill(0)
    }
}
