package ir.ornix.passgen.feature.home.impl.presentation

import ir.ornix.passgen.core.model.Account
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig

sealed interface HomeIntent {
    data class InputChanged(val input: String) : HomeIntent
    data class RemoveConfig(val passGenConfig: PassGenConfig) : HomeIntent
    data class SaveAccount(val account: Account) : HomeIntent
}
