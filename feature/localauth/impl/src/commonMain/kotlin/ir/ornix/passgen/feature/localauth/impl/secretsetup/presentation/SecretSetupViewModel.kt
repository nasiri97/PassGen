package ir.ornix.passgen.feature.localauth.impl.secretsetup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.domain.localauth.SaveLocalAuthSecretUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SecretSetupViewModel(
    private val saveLocalAuthSecret: SaveLocalAuthSecretUseCase
) : ViewModel() {


    private val intents = Channel<SecretSetupIntent>()

    val uiState: StateFlow<SecretSetupUiState>
        field = MutableStateFlow<SecretSetupUiState>(
            SecretSetupUiState(
                selectedSetupType = LocalAuthType.NONE,
                setupStage = SetupStage.CHOOSE_TYPE
            )
        )


    init {
        viewModelScope.launch {
            for (intent in intents) {
                when (intent) {

                }
            }
        }
    }

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


    /** Send new intent */
    fun dispatch(intent: SecretSetupIntent) = viewModelScope.launch {
        intents.send(intent)
    }

    private fun apply(change: SecretSetupPartialState) {
        uiState.update {
            reduce(oldState = it, change = change)
        }
    }
}