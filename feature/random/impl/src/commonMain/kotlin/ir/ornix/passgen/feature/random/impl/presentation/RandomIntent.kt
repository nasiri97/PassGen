package ir.ornix.passgen.feature.random.impl.presentation

import ir.ornix.passgen.core.model.Account
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig

sealed interface RandomIntent {
    data object RefreshAllPasswords : RandomIntent
    data object RefreshHintShown : RandomIntent
    data class RemoveConfig(val passGenConfig: PassGenConfig) : RandomIntent
    data class SaveAccount(val account: Account) : RandomIntent
}
