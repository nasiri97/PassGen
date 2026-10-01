package ir.ornix.passgen.feature.random.impl.presentation

import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.PassGenItem

internal sealed interface RandomPartialState {
    data object Refreshing : RandomPartialState
    data object Refreshed : RandomPartialState
    data object RefreshHintShown : RandomPartialState
    data class PasswordItemsLoaded(val passGenItems: List<PassGenItem>) : RandomPartialState
    data class PasswordUpdated(val configId: Int, val password: Password?) : RandomPartialState
}
