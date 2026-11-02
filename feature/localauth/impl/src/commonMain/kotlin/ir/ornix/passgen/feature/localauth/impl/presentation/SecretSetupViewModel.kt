package ir.ornix.passgen.feature.localauth.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.localauth.SaveLocalAuthSecretUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SecretSetupViewModel(
    private val saveLocalAuthSecret: SaveLocalAuthSecretUseCase
) : ViewModel() {

    private val intents = Channel<SecretSetupIntent>()

    internal val uiState: StateFlow<SecretSetupUiState>
        field = MutableStateFlow<SecretSetupUiState>(SecretSetupUiState())


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

    private suspend fun handleSecretInput(secret: String) {
        apply(SecretSetupPartialState.PasswordInputEnabledChanged(false))

        when (uiState.value.setupStage) {
            SetupStage.ENTER_SECRET -> {
                firstInputBuffer = secret
                apply(SecretSetupPartialState.SecretEntered)

                delay(500.milliseconds)
                apply(SecretSetupPartialState.PasswordInputEnabledChanged(true))
            }

            SetupStage.CONFIRM_SECRET -> {
                if (secret == firstInputBuffer) {
                    saveLocalAuthSecret(uiState.value.selectedSetupType, secret.encodeToByteArray())
                    apply(SecretSetupPartialState.ConfirmSecretMatched)
                } else {
                    apply(SecretSetupPartialState.ConfirmSecretMismatch(localAuthType = uiState.value.selectedSetupType))

                    delay(500.milliseconds)
                    apply(SecretSetupPartialState.PasswordInputEnabledChanged(true))
                }
            }

            else -> {}
        }
    }


    /** Send new intent */
    internal fun dispatch(intent: SecretSetupIntent) = viewModelScope.launch {
        intents.send(intent)
    }

    private fun apply(change: SecretSetupPartialState) {
        uiState.update {
            reduce(oldState = it, change = change)
        }
    }
}