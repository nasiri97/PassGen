package ir.ornix.passgen.feature.auth.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType

data class LocalAuthUiState(
    val currentLocalAuthType: LocalAuthType = LocalAuthType.NONE,
    val setupStage: SetupStage = SetupStage.CHOOSE_TYPE,
    val selectedSetupType: LocalAuthType,
    val isBiometricAvailable: Boolean,
    val isBiometricEnabled: Boolean,
    val isFirstLaunch: Boolean,
    val isSuccess: Boolean,
    val error: String? = null
)

enum class SetupStage {
    CHOOSE_TYPE,
    ENTER_SECRET,
    CONFIRM_SECRET,
    BIOMETRIC_SETUP
}
