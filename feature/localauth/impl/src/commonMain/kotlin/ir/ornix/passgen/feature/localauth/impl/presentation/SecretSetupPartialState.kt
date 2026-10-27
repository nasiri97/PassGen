package ir.ornix.passgen.feature.localauth.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType

internal sealed interface SecretSetupPartialState {

    data class LocalAuthTypeSelected(val localAuthType: LocalAuthType) : SecretSetupPartialState
    data class Error(val error: SecretSetupError?) : SecretSetupPartialState

    data object SetupCancelled : SecretSetupPartialState
    data object SecretEntered : SecretSetupPartialState
    data class ConfirmSecretMismatch(val localAuthType: LocalAuthType) : SecretSetupPartialState
    data object ConfirmSecretMatched : SecretSetupPartialState
}