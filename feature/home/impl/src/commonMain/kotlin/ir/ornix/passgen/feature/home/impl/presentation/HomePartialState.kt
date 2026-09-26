package ir.ornix.passgen.feature.home.impl.presentation

import ir.ornix.passgen.core.model.Password

internal sealed interface HomePartialState {
    data class InputChanged(val input: String) : HomePartialState

    data class PasswordItemsLoaded(val passwordItems: List<PasswordItem>) : HomePartialState
    data object Loading : HomePartialState
    data object ConfigCreated : HomePartialState
    data object ShowAddConfigDialog : HomePartialState
    data object HideAddConfigDialog : HomePartialState

    data class PasswordGenerated(val configId: Int, val password: Password?) : HomePartialState
    data class PasswordIsCalculating(val configId: Int) : HomePartialState
}