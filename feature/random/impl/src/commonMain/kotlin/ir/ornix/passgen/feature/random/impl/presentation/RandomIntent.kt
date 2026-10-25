package ir.ornix.passgen.feature.random.impl.presentation

import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig

internal sealed interface RandomIntent {
    data object RefreshAllPasswords : RandomIntent
    data object RefreshHintShown : RandomIntent
    data class RemoveConfig(val passGenConfig: PassGenConfig) : RandomIntent
    data class SaveCredential(val credential: Credential) : RandomIntent
}
