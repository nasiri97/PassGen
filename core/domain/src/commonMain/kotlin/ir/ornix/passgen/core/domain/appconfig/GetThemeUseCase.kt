package ir.ornix.passgen.core.domain.appconfig

import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.model.AppTheme
import kotlinx.coroutines.flow.StateFlow

class GetThemeUseCase(private val appConfigRepository: AppConfigRepository) {
    operator fun invoke(): StateFlow<AppTheme> = appConfigRepository.getTheme()
}
