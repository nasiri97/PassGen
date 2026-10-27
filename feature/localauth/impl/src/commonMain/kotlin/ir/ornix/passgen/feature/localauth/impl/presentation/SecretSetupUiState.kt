package ir.ornix.passgen.feature.localauth.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType

internal data class SecretSetupUiState(
    val selectedSetupType: LocalAuthType = LocalAuthType.NONE,
    val setupStage: SetupStage = SetupStage.CHOOSE_TYPE,
    val error: SecretSetupError? = null
)

enum class SetupStage {
    CHOOSE_TYPE,
    ENTER_SECRET,
    CONFIRM_SECRET,
    COMPLETED,
    CANCELLED
}
