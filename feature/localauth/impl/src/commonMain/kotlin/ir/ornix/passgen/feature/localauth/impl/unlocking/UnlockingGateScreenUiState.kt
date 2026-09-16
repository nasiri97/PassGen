package ir.ornix.passgen.feature.localauth.impl.unlocking

import ir.ornix.passgen.core.domain.LocalAuthType

data class UnlockingGateUiState(
    val localAuthType: LocalAuthType,
    val shouldShowBiometricOption: Boolean,
    val isLocked: Boolean = true,
    val errorMessage: String? = null
)