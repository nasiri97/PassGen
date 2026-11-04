package ir.ornix.passgen.feature.unlock.impl.presentation

internal fun reduce(
    oldState: UnlockingGateUiState,
    change: UnlockingGatePartialState
): UnlockingGateUiState {
    return when (change) {
        is UnlockingGatePartialState.AuthenticationFailed -> {
            oldState.copy(
                error = change.error
            )
        }

        UnlockingGatePartialState.AuthenticationSucceeded -> {
            oldState.copy(
                isLocked = false,
                error = null
            )
        }

        is UnlockingGatePartialState.BiometricEnabledChanged -> {
            oldState.copy(
                isBiometricEnabled = change.isEnabled
            )
        }

        is UnlockingGatePartialState.LocalAuthTypeChanged -> {
            oldState.copy(
                localAuthType = change.localAuthType
            )
        }

        is UnlockingGatePartialState.PasswordInputEnabledChanged -> {
            oldState.copy(
                isPasswordInputEnabled = change.isEnabled
            )
        }
    }
}