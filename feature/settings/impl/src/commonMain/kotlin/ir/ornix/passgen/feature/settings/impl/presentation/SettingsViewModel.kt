package ir.ornix.passgen.feature.settings.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.domain.localauth.GetLocalAuthTypeUseCase
import ir.ornix.passgen.core.domain.localauth.IsBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.SaveLocalAuthSecretUseCase
import ir.ornix.passgen.core.domain.localauth.SetBiometricEnabledUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val isBiometricEnabled: IsBiometricEnabledUseCase,
    private val setBiometricEnabled: SetBiometricEnabledUseCase,
    biometricAuthenticator: BiometricAuthenticator,
    getLocalAuthType: GetLocalAuthTypeUseCase,
    private val saveLocalAuthSecret: SaveLocalAuthSecretUseCase
) : ViewModel() {

    private val intents = Channel<SettingsIntent>()

    val uiState: StateFlow<SettingsUiState>
        field : MutableStateFlow<SettingsUiState> = MutableStateFlow(
            SettingsUiState(
                isBiometricAvailable = biometricAuthenticator.isBiometricAvailable(),
                isBiometricEnabled = isBiometricEnabled().value,
                currentLocalAuthType = getLocalAuthType().value
            )
        )


    init {
        viewModelScope.launch {
            isBiometricEnabled().collect {
                apply(SettingsPartialState.FingerprintEnabledChanged(it))
            }
        }

        viewModelScope.launch {
            getLocalAuthType().collect {
                apply(SettingsPartialState.LocalAuthenticationChanged(it))
            }
        }

        viewModelScope.launch {
            for (intent in intents) {
                when (intent) {
                    is SettingsIntent.SetFingerprintEnabled -> {
                        setBiometricEnabled(intent.enabled)
                    }

                    is SettingsIntent.LocalAuthenticationDisabled -> {
                        saveLocalAuthSecret(LocalAuthType.NONE, ByteArray(0))
                    }
                }
            }
        }
    }


    /** Send new intent */
    fun dispatch(intent: SettingsIntent) = viewModelScope.launch {
        intents.send(intent)
    }

    private fun apply(change: SettingsPartialState) {
        uiState.update {
            reduce(oldState = it, change = change)
        }
    }
}
