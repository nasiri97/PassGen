package ir.ornix.passgen.feature.unlock.impl.presentation

import ir.ornix.passgen.core.model.localauth.LocalAuthType

internal data class UnlockingGateUiState(
    val localAuthType: LocalAuthType = LocalAuthType.NONE,
    val isBiometricEnabled: Boolean = false,
    val isPasswordInputEnabled: Boolean = true,
    val isLocked: Boolean = true,
    val error: AuthenticationError? = null
)