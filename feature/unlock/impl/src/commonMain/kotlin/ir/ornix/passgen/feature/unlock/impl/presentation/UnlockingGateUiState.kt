package ir.ornix.passgen.feature.unlock.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType

data class UnlockingGateUiState(
    val localAuthType: LocalAuthType = LocalAuthType.NONE,
    val shouldShowBiometricOption: Boolean = false,
    val isLocked: Boolean = true,
    val errorMessage: String? = null
)