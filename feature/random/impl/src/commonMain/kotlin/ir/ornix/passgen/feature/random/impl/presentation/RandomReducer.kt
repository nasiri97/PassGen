package ir.ornix.passgen.feature.random.impl.presentation

internal fun reduce(
    oldState: RandomUiState,
    change: RandomPartialState
): RandomUiState {
    return when (change) {
        is RandomPartialState.Loading -> {
            oldState.copy(isLoading = false)
        }

        is RandomPartialState.PasswordItemsLoaded -> {
            oldState.copy(
                passwordItems = change.passwordItems
            )
        }
    }
}
