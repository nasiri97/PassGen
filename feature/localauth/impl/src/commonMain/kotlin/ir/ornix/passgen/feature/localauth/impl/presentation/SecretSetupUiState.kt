package ir.ornix.passgen.feature.localauth.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType

data class SecretSetupUiState(
    val selectedSetupType: LocalAuthType,
    val setupStage: SetupStage,
    val errorMessage: String? = null
)

enum class SetupStage {
    CHOOSE_TYPE,
    ENTER_SECRET,
    CONFIRM_SECRET,
    COMPLETED,
    CANCELLED
}
