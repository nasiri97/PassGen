package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.Crypto
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.data_management.PurgeSensitiveDataUseCase
import ir.ornix.passgen.core.logging.Logger
import ir.ornix.passgen.core.model.localauth.LocalAuthType
import kotlinx.coroutines.flow.first

class ValidateLocalAuthSecretUseCase(
    private val crypto: Crypto,
    private val repository: LocalAuthRepository,
    private val getLocalAuthType: GetLocalAuthTypeUseCase,
    private val purgeSensitiveData: PurgeSensitiveDataUseCase,
    private val isFailedAuthAttemptLimitExceeded: IsFailedAuthAttemptLimitExceededUseCase
) {
    suspend operator fun invoke(secret: ByteArray): Boolean {
        val encryptedSecret = repository.getEncryptedSecret() ?: run {
            Logger.i("No encrypted secret found")

            if (getLocalAuthType().first() != LocalAuthType.NONE) {
                purgeSensitiveData()
                Logger.w("The LocalAuthType is not NONE, but no encrypted secret was found. Purging sensitive data.")
            }

            return false
        }

        val realSecret = crypto.decrypt(encryptedSecret)
        val isValid = realSecret.contentEquals(secret)
        secret.fill(0)
        realSecret.fill(0)

        if (isValid) {
            repository.resetFailedAuthAttemptCount()
        } else {
            repository.incrementFailedAuthAttemptCount()
            Logger.i("Auth failed, Secrets do not match.")

            if (isFailedAuthAttemptLimitExceeded().first()) {
                purgeSensitiveData()
                Logger.w("Auth failed limit reached, purging sensitive data.")
            }
        }

        return isValid
    }
}
