package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.model.localauth.FailedAuthAttemptLimit
import ir.ornix.passgen.core.model.localauth.FailedAuthAttemptLimit.Companion.fromMaxAttempts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFailedAuthAttemptLimitUseCase(
    private val repository: LocalAuthRepository
) {
    operator fun invoke(): Flow<FailedAuthAttemptLimit> =
        repository.getFailedAuthAttemptLimit().map { maxAttempts ->
            fromMaxAttempts(maxAttempts)
        }
}