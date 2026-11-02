package ir.ornix.passgen.feature.home.impl.presentation

import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig

internal sealed interface HomeIntent {
    data class ContextInputChanged(val contextInput: String) : HomeIntent
    data class RemoveConfig(val passGenConfig: PassGenConfig) : HomeIntent
    data class SaveCredential(val credential: Credential) : HomeIntent
}
