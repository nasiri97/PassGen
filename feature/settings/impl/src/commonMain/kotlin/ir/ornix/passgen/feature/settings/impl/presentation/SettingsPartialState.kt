package ir.ornix.passgen.feature.settings.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout

internal sealed interface SettingsPartialState {
    data class FingerprintAvailabilityChanged(val available: Boolean) : SettingsPartialState
    data class FingerprintEnabledChanged(val enabled: Boolean) : SettingsPartialState
    data class LocalAuthenticationChanged(val type: LocalAuthType) : SettingsPartialState
    data class LanguageChanged(val language: AppLanguage) : SettingsPartialState
    data class ThemeChanged(val theme: AppTheme) : SettingsPartialState
    data class CopyEnabledChanged(val enabled: Boolean) : SettingsPartialState
    data class CopyWarningDialogVisibilityChanged(val visible: Boolean) : SettingsPartialState
    data class PasswordVisibilityTimeoutChanged(
        val timeout: PasswordVisibilityTimeout
    ) : SettingsPartialState
}
