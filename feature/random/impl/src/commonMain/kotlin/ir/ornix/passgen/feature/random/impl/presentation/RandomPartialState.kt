package ir.ornix.passgen.feature.random.impl.presentation

internal sealed interface RandomPartialState {
    data object Loading : RandomPartialState
    data class PasswordItemsLoaded(val passwordItems: List<PasswordItem>) : RandomPartialState
}
