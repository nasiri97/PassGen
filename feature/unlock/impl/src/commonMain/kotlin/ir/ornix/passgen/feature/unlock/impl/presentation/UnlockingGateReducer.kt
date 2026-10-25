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

        is UnlockingGatePartialState.BiometricOptionChanged -> {
            oldState.copy(
                shouldShowBiometricOption = change.shouldShowBiometricOption
            )
        }

        is UnlockingGatePartialState.LocalAuthTypeChanged -> {
            oldState.copy(
                localAuthType = change.localAuthType
            )
        }
    }
}