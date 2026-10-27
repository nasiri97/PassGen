package ir.ornix.passgen.core.domain.appconfig

import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.model.AppLanguage
import kotlinx.coroutines.flow.StateFlow

class GetLanguageUseCase(private val appConfigRepository: AppConfigRepository) {
    operator fun invoke(): StateFlow<AppLanguage> = appConfigRepository.getLanguage()
}
