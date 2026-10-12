package ir.ornix.passgen.feature.settings.impl.presentation

internal fun reduce(
    oldState: SettingsUiState,
    change: SettingsPartialState
): SettingsUiState {
    return when (change) {
        is SettingsPartialState.FingerprintEnabledChanged -> {
            oldState.copy(
                isBiometricEnabled = change.enabled
            )
        }

        is SettingsPartialState.LocalAuthenticationChanged -> {
            oldState.copy(
                currentLocalAuthType = change.type
            )
        }

        is SettingsPartialState.FingerprintAvailabilityChanged -> {
            oldState.copy(
                isBiometricAvailable = change.available
            )
        }

        is SettingsPartialState.LanguageChanged -> {
            oldState.copy(
                selectedLanguage = change.language
            )
        }

        is SettingsPartialState.ThemeChanged -> {
            oldState.copy(
                selectedTheme = change.theme
            )
        }

        is SettingsPartialState.CopyEnabledChanged -> {
            oldState.copy(
                isCopyEnabled = change.enabled
            )
        }

        is SettingsPartialState.CopyWarningDialogVisibilityChanged -> {
            oldState.copy(
                showCopyWarningDialog = change.visible
            )
        }

        is SettingsPartialState.PasswordVisibilityTimeoutChanged -> {
            oldState.copy(
                selectedVisibilityTimeout = change.timeout
            )
        }
    }
}
