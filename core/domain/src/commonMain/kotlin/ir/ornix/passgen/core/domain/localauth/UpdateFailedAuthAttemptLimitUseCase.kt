package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.model.localauth.FailedAuthAttemptLimit

class UpdateFailedAuthAttemptLimitUseCase(
    private val repository: LocalAuthRepository
) {
    suspend operator fun invoke(limit: FailedAuthAttemptLimit) {
        if (limit == FailedAuthAttemptLimit.DISABLED)
            repository.disableFailedAuthAttemptLimit()
        else
            repository.updateFailedAuthAttemptLimit(limit.maxAttempts)
    }
}