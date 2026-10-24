package ir.ornix.passgen.feature.home.impl.presentation

import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig

sealed interface HomeIntent {
    data class InputChanged(val input: String) : HomeIntent
    data class RemoveConfig(val passGenConfig: PassGenConfig) : HomeIntent
    data class SaveCredential(val credential: Credential) : HomeIntent
}
