package ir.ornix.passgen.feature.unlock.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.common.Platform
import ir.ornix.passgen.core.common.platform
import ir.ornix.passgen.core.domain.localauth.GetLocalAuthTypeUseCase
import ir.ornix.passgen.core.domain.localauth.IsBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.ValidateLocalAuthSecretUseCase
import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticationResult
import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticator
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.auth_use_biometric
import ir.ornix.passgen.core.ui.auth_use_biometric_or_screen_lock
import ir.ornix.passgen.core.ui.auth_verify_its_you
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import kotlin.time.Duration.Companion.milliseconds

class UnlockingGateViewModel(
    getLocalAuthType: GetLocalAuthTypeUseCase,
    isBiometricEnabled: IsBiometricEnabledUseCase,
    private val platformAuthenticator: PlatformAuthenticator,
    private val validateLocalAuthSecret: ValidateLocalAuthSecretUseCase
) : ViewModel() {

    private val intents = Channel<UnlockingGateIntent>()

    internal val uiState: StateFlow<UnlockingGateUiState>
        field : MutableStateFlow<UnlockingGateUiState> = MutableStateFlow(UnlockingGateUiState())

    init {
        viewModelScope.launch {
            isBiometricEnabled().collect { isEnabled ->
                apply(
                    UnlockingGatePartialState.BiometricEnabledChanged(isEnabled)
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
        apply(UnlockingGatePartialState.PasswordInputEnabledChanged(false))
        val isValid = validateLocalAuthSecret(secret.encodeToByteArray())

        if (isValid) {
            apply(UnlockingGatePartialState.AuthenticationSucceeded)
        } else {
            apply(
                UnlockingGatePartialState.AuthenticationFailed(
                    error = AuthenticationError.InvalidCredential(localAuthType = uiState.value.localAuthType)
                )
            )

            delay(500.milliseconds)
            apply(UnlockingGatePartialState.PasswordInputEnabledChanged(true))
        }
    }

    private suspend fun biometricClicked() {
        val description = when (platform) {
            is Platform.Android -> {
                if ((platform as Platform.Android).apiLevel >= 30) { // Build.VERSION_CODES.R
                    getString(Res.string.auth_use_biometric_or_screen_lock)
                } else {
                    getString(Res.string.auth_use_biometric)
                }
            }

            else -> getString(Res.string.auth_use_biometric)
        }

        platformAuthenticator.authenticate(
            title = getString(Res.string.auth_verify_its_you),
            subtitle = "",
            description = description,
            onResult = { result ->
                when (result) {
                    is PlatformAuthenticationResult.Success ->
                        apply(UnlockingGatePartialState.AuthenticationSucceeded)

                    is PlatformAuthenticationResult.Cancelled -> {}

                    is PlatformAuthenticationResult.Error ->
                        apply(
                            UnlockingGatePartialState.AuthenticationFailed(
                                error = AuthenticationError.BiometricAuthenticationError(reason = result.reason)
                            )
                        )
                }
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