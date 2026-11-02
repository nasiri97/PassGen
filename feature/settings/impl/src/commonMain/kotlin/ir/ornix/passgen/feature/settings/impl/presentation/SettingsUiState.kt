package ir.ornix.passgen.feature.settings.impl.presentation

import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout
import ir.ornix.passgen.core.model.localauth.FailedAuthAttemptLimit
import ir.ornix.passgen.core.model.localauth.LocalAuthType

internal data class SettingsUiState(
    val isBiometricAvailable: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val currentLocalAuthType: LocalAuthType = LocalAuthType.NONE,
    val selectedFailedAuthAttemptLimit: FailedAuthAttemptLimit = FailedAuthAttemptLimit.DISABLED,
    val selectedLanguage: AppLanguage = AppLanguage.SYSTEM,
    val selectedTheme: AppTheme = AppTheme.SYSTEM,
    val isCopyEnabled: Boolean = false,
    val showCopyWarningDialog: Boolean = false,
    val showPurgeSensitiveDataWarningDialog: Boolean = false,
    val selectedVisibilityTimeout: PasswordVisibilityTimeout = PasswordVisibilityTimeout.default,
    val isAutofillSupported: Boolean = false,
    val isAutofillEnabled: Boolean = true
) {
    val showAutofillBanner: Boolean
        get() = isAutofillSupported && !isAutofillEnabled
}
