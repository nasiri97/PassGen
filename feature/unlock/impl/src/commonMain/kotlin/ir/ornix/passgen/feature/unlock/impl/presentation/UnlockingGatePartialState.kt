package ir.ornix.passgen.feature.unlock.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType

internal sealed interface UnlockingGatePartialState {
    data object AuthenticationSucceeded : UnlockingGatePartialState
    data class AuthenticationFailed(val errorMessage: String?) : UnlockingGatePartialState
    data class LocalAuthTypeChanged(val localAuthType: LocalAuthType) : UnlockingGatePartialState
    data class BiometricOptionChanged(
        val shouldShowBiometricOption: Boolean
    ) : UnlockingGatePartialState
}