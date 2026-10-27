package ir.ornix.passgen.feature.unlock.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.localauth.GetLocalAuthTypeUseCase
import ir.ornix.passgen.core.domain.localauth.IsBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.ValidateLocalAuthSecretUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UnlockingGateViewModel(
    getLocalAuthType: GetLocalAuthTypeUseCase,
    isBiometricEnabled: IsBiometricEnabledUseCase,
    private val biometricAuthenticator: BiometricAuthenticator,
    private val validateLocalAuthSecret: ValidateLocalAuthSecretUseCase
) : ViewModel() {

    private val isBiometricAvailable = biometricAuthenticator.isBiometricAvailable()

    private val intents = Channel<UnlockingGateIntent>()

    internal val uiState: StateFlow<UnlockingGateUiState>
        field : MutableStateFlow<UnlockingGateUiState> = MutableStateFlow(UnlockingGateUiState())

    init {
        viewModelScope.launch {
            isBiometricEnabled().collect { isBiometricEnabled ->
                apply(
                    UnlockingGatePartialState.BiometricOptionChanged(
                        isBiometricAvailable && isBiometricEnabled
                    )
                )
            }
        }

        viewModelScope.launch {
            getLocalAuthType().collect { localAuthType ->
                apply(UnlockingGatePartialState.LocalAuthTypeChanged(localAuthType))
            }
        }

        viewModelScope.launch {
            for (intent in intents) {
                when (intent) {
                    is UnlockingGateIntent.BiometricClicked -> {
                        biometricClicked()
                    }

                    is UnlockingGateIntent.SecretSubmitted -> {
                        secretSubmitted(intent.secret)
                    }
                }
            }
        }
    }

    private suspend fun secretSubmitted(secret: String) {
        val isValid = validateLocalAuthSecret(secret.encodeToByteArray())

        if (isValid) {
            apply(UnlockingGatePartialState.AuthenticationSucceeded)
        } else {
            apply(
                UnlockingGatePartialState.AuthenticationFailed(
                    error = AuthenticationError.InvalidCredential(localAuthType = uiState.value.localAuthType)
                )
            )
        }
    }

    private fun biometricClicked() {
        if (!isBiometricAvailable) return

        biometricAuthenticator.authenticate(
            onSuccess = {
                apply(UnlockingGatePartialState.AuthenticationSucceeded)
            },
            onError = { _ ->
                apply(
                    UnlockingGatePartialState.AuthenticationFailed(
                        error = AuthenticationError.InvalidBiometric
                    )
                )
            }
        )
    }

    /** Send new intent */
    internal fun dispatch(intent: UnlockingGateIntent) = viewModelScope.launch {
        intents.send(intent)
    }

    private fun apply(change: UnlockingGatePartialState) {
        uiState.update {
            reduce(oldState = it, change = change)
        }
    }
}