package ir.ornix.passgen.feature.localauth.impl.unlocking.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.localauth.GetLocalAuthTypeUseCase
import ir.ornix.passgen.core.domain.localauth.IsBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.ValidateLocalAuthSecretUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UnlockingGateViewModel(
    getLocalAuthType: GetLocalAuthTypeUseCase,
    isBiometricEnabled: IsBiometricEnabledUseCase,
    private val biometricAuthenticator: BiometricAuthenticator,
    private val validateLocalAuthSecret: ValidateLocalAuthSecretUseCase
) : ViewModel() {

    private val localAuthTypeStateFlow = getLocalAuthType()
    private val isBiometricAvailableStateFlow = biometricAuthenticator.isBiometricAvailable()

    private val isBiometricEnabledStateFlow = isBiometricEnabled()

    private val intents = Channel<UnlockingGateIntent>()

    val uiState: StateFlow<UnlockingGateUiState>
        field : MutableStateFlow<UnlockingGateUiState> = MutableStateFlow(
            UnlockingGateUiState(
                localAuthType = localAuthTypeStateFlow.value,
                shouldShowBiometricOption = isBiometricAvailableStateFlow.value && isBiometricEnabledStateFlow.value,
                isLocked = true,
                errorMessage = null
            )
        )

    init {
        viewModelScope.launch {
            combine(
                isBiometricAvailableStateFlow,
                isBiometricEnabledStateFlow
            ) { isBiometricAvailable, isBiometricEnabled ->
                (isBiometricAvailable && isBiometricEnabled)
            }.collect { shouldShowBiometricOption ->
                apply(UnlockingGatePartialState.BiometricOptionChanged(shouldShowBiometricOption))
            }
        }

        viewModelScope.launch {
            localAuthTypeStateFlow.collect { localAuthType ->
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

    private fun secretSubmitted(secret: String) {
        val isValid = validateLocalAuthSecret(secret)

        if (isValid) {
            apply(UnlockingGatePartialState.AuthenticationSucceeded)
        } else {
            apply(
                UnlockingGatePartialState.AuthenticationFailed(
                    errorMessage = "Incorrect ${uiState.value.localAuthType.value}. Please try again."
                )
            )
        }
    }

    private fun biometricClicked() {
        if (!isBiometricAvailableStateFlow.value) return

        biometricAuthenticator.authenticate(
            onSuccess = {
                apply(UnlockingGatePartialState.AuthenticationSucceeded)
            },
            onError = { error ->
                apply(
                    UnlockingGatePartialState.AuthenticationFailed(
                        errorMessage = error
                    )
                )
            }
        )
    }

    /** Send new intent */
    fun dispatch(intent: UnlockingGateIntent) = viewModelScope.launch {
        intents.send(intent)
    }

    private fun apply(change: UnlockingGatePartialState) {
        uiState.update {
            reduce(oldState = it, change = change)
        }
    }
}