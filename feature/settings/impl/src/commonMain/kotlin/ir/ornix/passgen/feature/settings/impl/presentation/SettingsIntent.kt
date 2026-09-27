package ir.ornix.passgen.feature.settings.impl.presentation

sealed interface SettingsIntent {
    data class SetFingerprintEnabled(val enabled: Boolean) : SettingsIntent
    data object LocalAuthenticationDisabled : SettingsIntent
}