package ir.ornix.passgen.core.domain.appconfig

import ir.ornix.passgen.core.domain.AppConfigRepository

class SetFirstLaunchUseCase(private val repository: AppConfigRepository) {
    operator fun invoke(isFirstLaunch: Boolean) {
        repository.setFirstLaunch(isFirstLaunch)
    }
}
