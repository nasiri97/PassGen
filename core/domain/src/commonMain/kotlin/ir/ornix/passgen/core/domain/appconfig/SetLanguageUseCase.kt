package ir.ornix.passgen.core.domain.appconfig

import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.model.AppLanguage

class SetLanguageUseCase(private val appConfigRepository: AppConfigRepository) {
    operator fun invoke(language: AppLanguage) {
        appConfigRepository.setLanguage(language)
    }
}
