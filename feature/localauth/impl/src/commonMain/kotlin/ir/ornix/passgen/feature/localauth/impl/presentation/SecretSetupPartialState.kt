package ir.ornix.passgen.feature.localauth.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType

internal sealed interface SecretSetupPartialState {

    data class LocalAuthTypeSelected(val localAuthType: LocalAuthType) : SecretSetupPartialState
    data class Error(val errorMessage: String) : SecretSetupPartialState

    data object SetupCancelled : SecretSetupPartialState
    data object SecretEntered : SecretSetupPartialState
    data object ConfirmSecretMismatch : SecretSetupPartialState
    data object ConfirmSecretMatched : SecretSetupPartialState
}