package ir.ornix.passgen.feature.localauth.impl.secretsetup.presentation

import ir.ornix.passgen.core.domain.LocalAuthType

internal fun reduce(
    oldState: SecretSetupUiState,
    change: SecretSetupPartialState
): SecretSetupUiState {
    return when (change) {

        is SecretSetupPartialState.LocalAuthTypeSelected -> {
            if (change.localAuthType == LocalAuthType.NONE) {
                oldState.copy(
                    selectedSetupType = change.localAuthType,
                    setupStage = SetupStage.CANCELLED,
                    errorMessage = null
                )
            } else {
                oldState.copy(
                    selectedSetupType = change.localAuthType,
                    setupStage = SetupStage.ENTER_SECRET,
                    errorMessage = null
                )
            }
        }


        SecretSetupPartialState.SetupCancelled -> {
            oldState.copy(
                selectedSetupType = LocalAuthType.NONE,
                setupStage = SetupStage.CANCELLED,
                errorMessage = null
            )
        }


        is SecretSetupPartialState.Error -> {
            oldState.copy(
                errorMessage = change.errorMessage
            )
        }

        is SecretSetupPartialState.SecretEntered -> {
            oldState.copy(
                setupStage = SetupStage.CONFIRM_SECRET,
                errorMessage = null
            )
        }

        is SecretSetupPartialState.ConfirmSecretMismatch -> {
            oldState.copy(
                errorMessage = "Secrets do not match. Please try again.",
                setupStage = SetupStage.ENTER_SECRET
            )
        }

        is SecretSetupPartialState.ConfirmSecretMatched -> {
            oldState.copy(
                setupStage = SetupStage.COMPLETED,
                errorMessage = null
            )
        }
    }
}