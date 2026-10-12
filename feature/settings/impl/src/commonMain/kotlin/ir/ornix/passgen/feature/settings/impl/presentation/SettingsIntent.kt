package ir.ornix.passgen.feature.settings.impl.presentation

import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout

sealed interface SettingsIntent {
    data class SetFingerprintEnabled(val enabled: Boolean) : SettingsIntent
    data object LocalAuthenticationDisabled : SettingsIntent
    data class SelectLanguage(val language: AppLanguage) : SettingsIntent
    data class SelectTheme(val theme: AppTheme) : SettingsIntent
    data class RequestSetCopyEnabled(val enabled: Boolean) : SettingsIntent
    data object ConfirmEnableCopy : SettingsIntent
    data object DismissCopyWarningDialog : SettingsIntent
    data class SelectVisibilityTimeout(val timeout: PasswordVisibilityTimeout) : SettingsIntent
}
