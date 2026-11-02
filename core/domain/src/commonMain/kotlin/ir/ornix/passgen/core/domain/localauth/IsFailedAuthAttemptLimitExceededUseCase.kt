package ir.ornix.passgen.core.domain.localauth

import ir.ornix.passgen.core.domain.LocalAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class IsFailedAuthAttemptLimitExceededUseCase(
    private val repository: LocalAuthRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return combine(
            repository.getFailedAuthAttemptLimit(),
            repository.getFailedAuthAttemptCount()
        ) { limit, count ->
            count >= limit
        }
    }
}