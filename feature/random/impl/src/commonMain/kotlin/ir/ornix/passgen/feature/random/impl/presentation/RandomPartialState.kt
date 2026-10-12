package ir.ornix.passgen.feature.random.impl.presentation

import ir.ornix.passgen.core.model.PassGenItem
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout

internal sealed interface RandomPartialState {
    data object Refreshing : RandomPartialState
    data object Refreshed : RandomPartialState
    data object RefreshHintShown : RandomPartialState
    data class PasswordItemsLoaded(val passGenItems: List<PassGenItem>) : RandomPartialState
    data class PasswordUpdated(val configId: Int, val password: Password?) : RandomPartialState
    data class CopyEnabledChanged(val isCopyEnabled: Boolean) : RandomPartialState

    data class PasswordVisibilityTimeoutChanged(
        val visibilityTimeout: PasswordVisibilityTimeout
    ) : RandomPartialState
}
