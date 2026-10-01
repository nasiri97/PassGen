package ir.ornix.passgen.feature.random.impl.presentation

internal fun reduce(
    oldState: RandomUiState,
    change: RandomPartialState
): RandomUiState {
    return when (change) {
        is RandomPartialState.Refreshing ->
            oldState.copy(isRefreshing = true)

        is RandomPartialState.Refreshed ->
            oldState.copy(isRefreshing = false)

        is RandomPartialState.PasswordItemsLoaded -> {
            oldState.copy(
                isRefreshing = false,
                passwordItems = change.passwordItems
            )
        }

        is RandomPartialState.PasswordUpdated -> {
            val updatedPasswordItems = oldState.passwordItems.map { item ->
                if (item.config.id == change.configId)
                    item.copy(password = change.password)
                else item
            }

            oldState.copy(
                passwordItems = updatedPasswordItems
            )
        }
    }
}
