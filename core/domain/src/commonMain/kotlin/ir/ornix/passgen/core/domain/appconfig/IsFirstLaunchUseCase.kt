package ir.ornix.passgen.core.domain.appconfig

import ir.ornix.passgen.core.domain.AppConfigRepository
import kotlinx.coroutines.flow.StateFlow

class IsFirstLaunchUseCase(private val repository: AppConfigRepository) {
    operator fun invoke(): StateFlow<Boolean> = repository.isFirstLaunch()
}
