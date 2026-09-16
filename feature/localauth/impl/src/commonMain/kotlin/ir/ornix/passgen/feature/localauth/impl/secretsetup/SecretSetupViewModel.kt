package ir.ornix.passgen.feature.localauth.impl.secretsetup

import androidx.lifecycle.ViewModel
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.domain.localauth.SaveLocalAuthSecretUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SecretSetupViewModel(
    private val saveLocalAuthSecret: SaveLocalAuthSecretUseCase
) : ViewModel() {

    val uiState: StateFlow<SetSecretUiSate>
        field = MutableStateFlow<SetSecretUiSate>(
            SetSecretUiSate(
                selectedSetupType = LocalAuthType.NONE,
                setupStage = SetupStage.CHOOSE_TYPE
            )
        )

    fun selectSetupType(type: LocalAuthType) {
        if (type == LocalAuthType.NONE) {
            //
        } else {
            uiState.value = uiState.value.copy(
                selectedSetupType = type,
                setupStage = SetupStage.ENTER_SECRET,
                errorMessage = null
            )
        }
    }

    private var firstInputBuffer: String = ""

    fun handleSecretInput(secret: String) {
        if (uiState.value.setupStage == SetupStage.ENTER_SECRET) {
            firstInputBuffer = secret
            uiState.value = uiState.value.copy(
                setupStage = SetupStage.CONFIRM_SECRET,
                errorMessage = null
            )
        } else if (uiState.value.setupStage == SetupStage.CONFIRM_SECRET) {
            if (secret == firstInputBuffer) {
                saveLocalAuthSecret(uiState.value.selectedSetupType, secret)
                uiState.value = uiState.value.copy(
                    setupStage = SetupStage.COMPLETED,
                    errorMessage = null
                )
            } else {
                uiState.value = uiState.value.copy(
                    errorMessage = "Secrets do not match. Please try again.",
                    setupStage = SetupStage.ENTER_SECRET
                )
            }
        }

    }

    fun cancelSetup() {
        uiState.value = uiState.value.copy(
            selectedSetupType = LocalAuthType.NONE,
            setupStage = SetupStage.CANCELLED,
            errorMessage = null
        )
    }
}