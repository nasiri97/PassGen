package ir.ornix.passgen.feature.auth.impl.presentation

import androidx.lifecycle.ViewModel
import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.domain.appconfig.IsFirstLaunchUseCase
import ir.ornix.passgen.core.domain.appconfig.SetFirstLaunchUseCase
import ir.ornix.passgen.core.domain.localauth.GetBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.GetLocalAuthTypeUseCase
import ir.ornix.passgen.core.domain.localauth.SaveLocalAuthSecretUseCase
import ir.ornix.passgen.core.domain.localauth.SetBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.ValidateLocalAuthSecretUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class LocalAuthViewModel(
    private val isFirstLaunch: IsFirstLaunchUseCase,
    private val setFirstLaunch: SetFirstLaunchUseCase,
    private val getLocalAuthType: GetLocalAuthTypeUseCase,
    private val saveLocalAuthSecret: SaveLocalAuthSecretUseCase,
    private val validateLocalAuthSecret: ValidateLocalAuthSecretUseCase,
    private val getBiometricEnabled: GetBiometricEnabledUseCase,
    private val setBiometricEnabled: SetBiometricEnabledUseCase,
    private val biometricAuthenticator: BiometricAuthenticator
) : ViewModel() {

    val uiState: StateFlow<LocalAuthUiState>
        field  : MutableStateFlow<LocalAuthUiState> = MutableStateFlow(
            LocalAuthUiState(
                currentLocalAuthType = getLocalAuthType(),
                selectedSetupType = LocalAuthType.PIN,
                isBiometricAvailable = biometricAuthenticator.isBiometricAvailable(),
                isBiometricEnabled = getBiometricEnabled(),
                isFirstLaunch = isFirstLaunch(),
                isSuccess = false
            )
        )

    private var firstInputBuffer: String = ""


    init {
        if (!uiState.value.isFirstLaunch &&
            !uiState.value.isBiometricEnabled &&
            uiState.value.currentLocalAuthType == LocalAuthType.NONE
        ) {
            uiState.value = uiState.value.copy(isSuccess = true)
        }
    }

    fun selectSetupType(type: LocalAuthType) {
        if (type == LocalAuthType.NONE) {
            // Skip PIN/Pattern setup
            goToBiometricSetup()
        } else {
            uiState.value = uiState.value.copy(
                selectedSetupType = type,
                setupStage = SetupStage.ENTER_SECRET,
                error = null
            )
        }
    }

    fun handleSecretInput(secret: String) {
        val current = uiState.value
        if (current.isFirstLaunch) {
            if (current.setupStage == SetupStage.ENTER_SECRET) {
                firstInputBuffer = secret
                uiState.value = current.copy(
                    setupStage = SetupStage.CONFIRM_SECRET,
                    error = null
                )
            } else if (current.setupStage == SetupStage.CONFIRM_SECRET) {
                if (secret == firstInputBuffer) {
                    saveLocalAuthSecret(current.selectedSetupType, secret)
                    goToBiometricSetup()
                } else {
                    uiState.value = current.copy(
                        error = "Secrets do not match. Please try again.",
                        setupStage = SetupStage.ENTER_SECRET
                    )
                }
            }
        } else {
            val isValid = validateLocalAuthSecret(secret)
            if (isValid) {
                uiState.value = current.copy(isSuccess = true, error = null)
            } else {
                uiState.value =
                    current.copy(error = "Incorrect PIN, password, or pattern. Please try again.")
            }
        }
    }

    private fun goToBiometricSetup() {
        if (uiState.value.isBiometricAvailable) {
            uiState.value = uiState.value.copy(
                setupStage = SetupStage.BIOMETRIC_SETUP,
                isBiometricEnabled = true // Default to ON in first setup as requested
            )
        } else {
            completeSetup()
        }
    }

    fun setBiometricInSetup(enabled: Boolean) {
        uiState.value = uiState.value.copy(isBiometricEnabled = enabled)
    }

    fun completeSetup() {
        setBiometricEnabled(uiState.value.isBiometricEnabled)
        uiState.value = uiState.value.copy(isSuccess = true)
        setFirstLaunch(false)
    }

    fun triggerBiometricPrompt() {
        if (!biometricAuthenticator.isBiometricAvailable()) return
        biometricAuthenticator.authenticate(
            onSuccess = {
                uiState.value = uiState.value.copy(isSuccess = true, error = null)
            },
            onError = { err ->
                uiState.value = uiState.value.copy(error = err)
            }
        )
    }
}
