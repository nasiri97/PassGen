package ir.ornix.passgen.feature.unlock.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType

internal data class UnlockingGateUiState(
    val localAuthType: LocalAuthType = LocalAuthType.NONE,
    val shouldShowBiometricOption: Boolean = false,
    val isLocked: Boolean = true,
    val error: AuthenticationError? = null
)