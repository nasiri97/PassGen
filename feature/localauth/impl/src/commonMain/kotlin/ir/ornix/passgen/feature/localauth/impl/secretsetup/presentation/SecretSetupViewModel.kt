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
                    is SecretSetupIntent.LocalAuthTypeSelected -> {
                        apply(SecretSetupPartialState.LocalAuthTypeSelected(intent.localAuthType))
                    }

                    is SecretSetupIntent.SecretInserted -> {
                        handleSecretInput(intent.secret)
                    }

                    SecretSetupIntent.SetupCancelled -> {
                        apply(SecretSetupPartialState.SetupCancelled)
                    }
                }
            }
        }
    }


    private var firstInputBuffer: String = ""

    private fun handleSecretInput(secret: String) {

        when (uiState.value.setupStage) {
            SetupStage.ENTER_SECRET -> {
                firstInputBuffer = secret
                apply(SecretSetupPartialState.SecretEntered)
            }

            SetupStage.CONFIRM_SECRET -> {
                if (secret == firstInputBuffer) {
                    saveLocalAuthSecret(uiState.value.selectedSetupType, secret)
                    apply(SecretSetupPartialState.ConfirmSecretMatched)
                } else {
                    apply(SecretSetupPartialState.ConfirmSecretMismatch)
                }
            }

            else -> {}
        }
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