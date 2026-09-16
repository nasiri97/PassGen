package ir.ornix.passgen.feature.settings.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.domain.localauth.GetLocalAuthTypeUseCase
import ir.ornix.passgen.core.domain.localauth.IsBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.SaveLocalAuthSecretUseCase
import ir.ornix.passgen.core.domain.localauth.SetBiometricEnabledUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val isBiometricEnabled: IsBiometricEnabledUseCase,
    private val setBiometricEnabled: SetBiometricEnabledUseCase,
    biometricAuthenticator: BiometricAuthenticator,
    getLocalAuthType: GetLocalAuthTypeUseCase,
    private val saveLocalAuthSecret: SaveLocalAuthSecretUseCase
) : ViewModel() {


    private val isBiometricAvailableStateFlow =
        biometricAuthenticator.isBiometricAvailable()

    private val isBiometricEnabledStateFlow =
        isBiometricEnabled()

    private val localAuthTypeStateFlow =
        getLocalAuthType()

    val uiState: StateFlow<SettingsUiState> =
        combine(
            isBiometricAvailableStateFlow,
            isBiometricEnabledStateFlow,
            localAuthTypeStateFlow
        ) { isAvailable, isEnabled, authType ->
            SettingsUiState(
                isBiometricAvailable = isAvailable,
                isBiometricEnabled = isEnabled,
                currentLocalAuthType = authType
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(
                isBiometricAvailable = isBiometricAvailableStateFlow.value,
                isBiometricEnabled = isBiometricEnabledStateFlow.value,
                currentLocalAuthType = localAuthTypeStateFlow.value
            )
        )

    fun toggleBiometricSetting(enabled: Boolean) {
        setBiometricEnabled(enabled)
    }

    fun toggleLocalAuthSetting(enabled: Boolean) {
        if (!enabled) {
            saveLocalAuthSecret(LocalAuthType.NONE, "")
        }
    }
}
