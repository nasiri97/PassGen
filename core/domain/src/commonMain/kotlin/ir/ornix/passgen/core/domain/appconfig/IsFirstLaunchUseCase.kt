package ir.ornix.passgen.core.domain.appconfig

import ir.ornix.passgen.core.domain.AppConfigRepository

class IsFirstLaunchUseCase(private val repository: AppConfigRepository) {
    operator fun invoke(): Boolean = repository.isFirstLaunch()
}
