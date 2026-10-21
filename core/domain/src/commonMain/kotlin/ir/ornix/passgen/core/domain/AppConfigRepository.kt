package ir.ornix.passgen.core.domain

import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout
import kotlinx.coroutines.flow.StateFlow

interface AppConfigRepository {
    fun getLanguage(): StateFlow<AppLanguage>
    fun setLanguage(language: AppLanguage)
    fun getTheme(): StateFlow<AppTheme>
    fun setTheme(theme: AppTheme)
    fun isCopyEnabled(): StateFlow<Boolean>
    fun setCopyEnabled(enabled: Boolean)
    fun getVisibilityTimeout(): StateFlow<PasswordVisibilityTimeout>
    fun setVisibilityTimeout(timeout: PasswordVisibilityTimeout)
}
