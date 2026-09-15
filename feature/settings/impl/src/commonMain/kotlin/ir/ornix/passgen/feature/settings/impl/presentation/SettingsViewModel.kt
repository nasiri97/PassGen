package ir.ornix.passgen.feature.settings.impl.presentation

import androidx.lifecycle.ViewModel
import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.domain.localauth.GetBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.GetLocalAuthTypeUseCase
import ir.ornix.passgen.core.domain.localauth.SaveLocalAuthSecretUseCase
import ir.ornix.passgen.core.domain.localauth.SetBiometricEnabledUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val getBiometricEnabled: GetBiometricEnabledUseCase,
    private val setBiometricEnabled: SetBiometricEnabledUseCase,
    private val biometricAuthenticator: BiometricAuthenticator,
    private val getLocalAuthType: GetLocalAuthTypeUseCase,
    private val saveLocalAuthSecret: SaveLocalAuthSecretUseCase,
) : ViewModel() {


    val uiState: StateFlow<SettingsUiState>
        field = MutableStateFlow<SettingsUiState>(
            SettingsUiState(
                isBiometricAvailable = biometricAuthenticator.isBiometricAvailable(),
                isBiometricEnabled = getBiometricEnabled(),
                currentLocalAuthType = getLocalAuthType()
            )
        )

    fun toggleBiometricSetting(enabled: Boolean) {
        setBiometricEnabled(enabled)
        uiState.value = uiState.value.copy(isBiometricEnabled = enabled)
    }

    fun toggleLocalAuthSetting(enabled: Boolean) {
        if (!enabled) {
            saveLocalAuthSecret(LocalAuthType.NONE, "")
            uiState.value = uiState.value.copy(currentLocalAuthType = LocalAuthType.NONE)
        } else {
            //
        }
    }
}
