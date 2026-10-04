package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import kotlinx.coroutines.flow.StateFlow

interface AppConfigRepository {
    fun isFirstLaunch(): Boolean
    fun setFirstLaunch(isFirstLaunch: Boolean)
    fun getLanguage(): StateFlow<AppLanguage>
    fun setLanguage(language: AppLanguage)
    fun getTheme(): StateFlow<AppTheme>
    fun setTheme(theme: AppTheme)
}
