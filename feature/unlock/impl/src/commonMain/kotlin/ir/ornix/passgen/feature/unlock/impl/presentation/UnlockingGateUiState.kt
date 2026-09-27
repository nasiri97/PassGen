package ir.ornix.passgen.feature.unlock.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType

data class UnlockingGateUiState(
    val localAuthType: LocalAuthType,
    val shouldShowBiometricOption: Boolean,
    val isLocked: Boolean,
    val errorMessage: String?
)