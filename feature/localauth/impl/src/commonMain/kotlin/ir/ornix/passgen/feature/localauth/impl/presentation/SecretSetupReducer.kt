package ir.ornix.passgen.feature.localauth.impl.presentation

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
                    error = null
                )
            } else {
                oldState.copy(
                    selectedSetupType = change.localAuthType,
                    setupStage = SetupStage.ENTER_SECRET,
                    error = null
                )
            }
        }


        SecretSetupPartialState.SetupCancelled -> {
            oldState.copy(
                selectedSetupType = LocalAuthType.NONE,
                setupStage = SetupStage.CANCELLED,
                error = null
            )
        }


        is SecretSetupPartialState.Error -> {
            oldState.copy(
                error = change.error
            )
        }

        is SecretSetupPartialState.SecretEntered -> {
            oldState.copy(
                setupStage = SetupStage.CONFIRM_SECRET,
                error = null
            )
        }

        is SecretSetupPartialState.ConfirmSecretMismatch -> {
            oldState.copy(
                error = SecretSetupError.SecretsNotMatch(localAuthType = change.localAuthType),
                setupStage = SetupStage.ENTER_SECRET
            )
        }

        is SecretSetupPartialState.ConfirmSecretMatched -> {
            oldState.copy(
                setupStage = SetupStage.COMPLETED,
                error = null
            )
        }
    }
}