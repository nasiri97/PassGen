package ir.ornix.passgen.feature.settings.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType

internal sealed interface SettingsPartialState {

    data class FingerprintAvailabilityChanged(val available: Boolean) : SettingsPartialState
    data class FingerprintEnabledChanged(val enabled: Boolean) : SettingsPartialState
    data class LocalAuthenticationChanged(val type: LocalAuthType) : SettingsPartialState

    data object NavigateToSetPassword : SettingsPartialState
    data object SettingPasswordCompleted : SettingsPartialState
}