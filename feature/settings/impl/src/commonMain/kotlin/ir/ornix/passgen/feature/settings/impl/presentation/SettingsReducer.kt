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

        is SettingsPartialState.NavigateToSetPassword -> {
            oldState.copy(
                isSettingPassInProgress = true
            )
        }

        is SettingsPartialState.SettingPasswordCompleted -> {
            oldState.copy(
                isSettingPassInProgress = false
            )
        }
    }
}