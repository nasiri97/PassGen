package ir.ornix.passgen.core.domain.appconfig

import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout

class SetVisibilityTimeoutUseCase(private val repository: AppConfigRepository) {
    operator fun invoke(timeout: PasswordVisibilityTimeout) {
        repository.setVisibilityTimeout(timeout)
    }
}
