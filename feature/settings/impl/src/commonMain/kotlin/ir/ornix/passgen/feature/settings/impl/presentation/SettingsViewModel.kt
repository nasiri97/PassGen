package ir.ornix.passgen.feature.settings.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.domain.appconfig.GetLanguageUseCase
import ir.ornix.passgen.core.domain.appconfig.GetThemeUseCase
import ir.ornix.passgen.core.domain.appconfig.GetVisibilityTimeoutUseCase
import ir.ornix.passgen.core.domain.appconfig.IsCopyEnabledUseCase
import ir.ornix.passgen.core.domain.appconfig.SetCopyEnabledUseCase
import ir.ornix.passgen.core.domain.appconfig.SetLanguageUseCase
import ir.ornix.passgen.core.domain.appconfig.SetThemeUseCase
import ir.ornix.passgen.core.domain.appconfig.SetVisibilityTimeoutUseCase
import ir.ornix.passgen.core.domain.localauth.GetLocalAuthTypeUseCase
import ir.ornix.passgen.core.domain.localauth.IsBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.SaveLocalAuthSecretUseCase
import ir.ornix.passgen.core.domain.localauth.SetBiometricEnabledUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val isBiometricEnabled: IsBiometricEnabledUseCase,
    private val setBiometricEnabled: SetBiometricEnabledUseCase,
    biometricAuthenticator: BiometricAuthenticator,
    getLocalAuthType: GetLocalAuthTypeUseCase,
    private val saveLocalAuthSecret: SaveLocalAuthSecretUseCase,
    getLanguage: GetLanguageUseCase,
    private val setLanguage: SetLanguageUseCase,
    getTheme: GetThemeUseCase,
    private val setTheme: SetThemeUseCase,
    isCopyEnabled: IsCopyEnabledUseCase,
    private val setCopyEnabled: SetCopyEnabledUseCase,
    getVisibilityTimeout: GetVisibilityTimeoutUseCase,
    private val setVisibilityTimeout: SetVisibilityTimeoutUseCase
) : ViewModel() {

    private val intents = Channel<SettingsIntent>()

    internal val uiState: StateFlow<SettingsUiState>
        field : MutableStateFlow<SettingsUiState> = MutableStateFlow(
            SettingsUiState(
                isBiometricAvailable = biometricAuthenticator.isBiometricAvailable()
            )
        )

    init {
        // Observe CopyEnabled
        isCopyEnabled().onEach { enabled ->
            apply(SettingsPartialState.CopyEnabledChanged(enabled))
        }.launchIn(viewModelScope)

        // Observe password visibility timeout
        getVisibilityTimeout().onEach { timeout ->
            apply(SettingsPartialState.PasswordVisibilityTimeoutChanged(timeout))
        }.launchIn(viewModelScope)

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
            getLanguage().collect {
                apply(SettingsPartialState.LanguageChanged(it))
            }
        }

        viewModelScope.launch {
            getTheme().collect {
                apply(SettingsPartialState.ThemeChanged(it))
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

                    is SettingsIntent.SelectLanguage -> {
                        setLanguage(intent.language)
                    }

                    is SettingsIntent.SelectTheme -> {
                        setTheme(intent.theme)
                    }

                    is SettingsIntent.RequestSetCopyEnabled -> {
                        if (intent.enabled) {
                            if (!uiState.value.isCopyEnabled) {
                                apply(SettingsPartialState.CopyWarningDialogVisibilityChanged(true))
                            }
                        } else {
                            setCopyEnabled(false)
                        }
                    }

                    is SettingsIntent.ConfirmEnableCopy -> {
                        apply(SettingsPartialState.CopyWarningDialogVisibilityChanged(false))
                        setCopyEnabled(true)
                    }

                    is SettingsIntent.DismissCopyWarningDialog -> {
                        apply(SettingsPartialState.CopyWarningDialogVisibilityChanged(false))
                    }

                    is SettingsIntent.SelectVisibilityTimeout -> {
                        setVisibilityTimeout(intent.timeout)
                    }
                }
            }
        }
    }

    /** Send new intent */
    internal fun dispatch(intent: SettingsIntent) = viewModelScope.launch {
        intents.send(intent)
    }

    private fun apply(change: SettingsPartialState) {
        uiState.update {
            reduce(oldState = it, change = change)
        }
    }
}
