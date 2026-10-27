package ir.ornix.passgen.core.domain.appconfig

import ir.ornix.passgen.core.domain.AppConfigRepository

class SetCopyEnabledUseCase(private val repository: AppConfigRepository) {
    operator fun invoke(enabled: Boolean) {
        repository.setCopyEnabled(enabled)
    }
}