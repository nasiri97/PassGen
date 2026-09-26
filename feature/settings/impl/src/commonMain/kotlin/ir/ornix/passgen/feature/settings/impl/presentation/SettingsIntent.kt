package ir.ornix.passgen.feature.settings.impl.presentation

sealed interface SettingsIntent {
    data class SetFingerprintEnabled(val enabled: Boolean) : SettingsIntent
    data class SetLocalAuthenticationEnabled(val enabled: Boolean) : SettingsIntent
    data object NavigateToSetPassword : SettingsIntent
    data object SettingPasswordCompleted : SettingsIntent
}