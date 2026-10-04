package ir.ornix.passgen.feature.settings.impl.presentation

import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme

sealed interface SettingsIntent {
    data class SetFingerprintEnabled(val enabled: Boolean) : SettingsIntent
    data object LocalAuthenticationDisabled : SettingsIntent
    data class SelectLanguage(val language: AppLanguage) : SettingsIntent
    data class SelectTheme(val theme: AppTheme) : SettingsIntent
}
