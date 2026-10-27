package ir.ornix.passgen.core.domain.appconfig

import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.model.AppTheme

class SetThemeUseCase(private val appConfigRepository: AppConfigRepository) {
    operator fun invoke(theme: AppTheme) {
        appConfigRepository.setTheme(theme)
    }
}
