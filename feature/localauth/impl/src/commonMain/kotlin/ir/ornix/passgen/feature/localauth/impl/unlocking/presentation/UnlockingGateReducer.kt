package ir.ornix.passgen.feature.localauth.impl.unlocking.presentation

internal fun reduce(
    oldState: UnlockingGateUiState,
    change: UnlockingGatePartialState
): UnlockingGateUiState {
    return when (change) {
        is UnlockingGatePartialState.AuthenticationFailed -> {
            oldState.copy(
                errorMessage = change.errorMessage,
            )
        }

        UnlockingGatePartialState.AuthenticationSucceeded -> {
            oldState.copy(
                isLocked = false,
                errorMessage = null
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