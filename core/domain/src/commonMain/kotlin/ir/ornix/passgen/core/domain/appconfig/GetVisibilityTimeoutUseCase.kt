package ir.ornix.passgen.core.domain.appconfig

import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout
import kotlinx.coroutines.flow.StateFlow

class GetVisibilityTimeoutUseCase(private val repository: AppConfigRepository) {
    operator fun invoke(): StateFlow<PasswordVisibilityTimeout> = repository.getVisibilityTimeout()
}