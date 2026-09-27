package ir.ornix.passgen.feature.settings.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType

data class SettingsUiState(
    val isBiometricAvailable: Boolean,
    val isBiometricEnabled: Boolean,
    val currentLocalAuthType: LocalAuthType
)