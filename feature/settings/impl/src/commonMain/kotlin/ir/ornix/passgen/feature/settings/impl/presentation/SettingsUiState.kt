package ir.ornix.passgen.feature.settings.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout

data class SettingsUiState(
    val isBiometricAvailable: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val currentLocalAuthType: LocalAuthType = LocalAuthType.NONE,
    val selectedLanguage: AppLanguage = AppLanguage.SYSTEM,
    val selectedTheme: AppTheme = AppTheme.SYSTEM,
    val isCopyEnabled: Boolean = false,
    val showCopyWarningDialog: Boolean = false,
    val selectedVisibilityTimeout: PasswordVisibilityTimeout = PasswordVisibilityTimeout.default
)
