package ir.ornix.passgen.feature.localauth.impl.unlocking

import androidx.lifecycle.ViewModel
import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.localauth.GetLocalAuthTypeUseCase
import ir.ornix.passgen.core.domain.localauth.IsBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.ValidateLocalAuthSecretUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UnlockingGateViewModel(
    private val getLocalAuthType: GetLocalAuthTypeUseCase,
    private val isBiometricEnabled: IsBiometricEnabledUseCase,
    private val biometricAuthenticator: BiometricAuthenticator,
    private val validateLocalAuthSecret: ValidateLocalAuthSecretUseCase
) : ViewModel() {

    val uiState: StateFlow<UnlockingGateUiState>
        field = MutableStateFlow<UnlockingGateUiState>(
            UnlockingGateUiState(
                localAuthType = getLocalAuthType(),
                shouldShowBiometricOption = biometricAuthenticator.isBiometricAvailable() && isBiometricEnabled()
            )
        )

    fun onSecretSubmitted(secret: String) {
        val isValid = validateLocalAuthSecret(secret)

        if (isValid) {
            uiState.value = uiState.value.copy(
                isLocked = false,
                errorMessage = null
            )
        } else {
            uiState.value = uiState.value.copy(
                errorMessage = "Incorrect ${uiState.value.localAuthType.value}. Please try again.",
            )
        }
    }

    fun onBiometricClick() {
        if (!biometricAuthenticator.isBiometricAvailable()) return

        biometricAuthenticator.authenticate(
            onSuccess = {
                uiState.value = uiState.value.copy(
                    isLocked = false,
                    errorMessage = null
                )
            },
            onError = { error ->
                uiState.value = uiState.value.copy(
                    errorMessage = error,
                )
            }
        )
    }
}