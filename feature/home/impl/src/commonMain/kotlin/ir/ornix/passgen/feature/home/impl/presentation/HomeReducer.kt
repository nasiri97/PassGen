package ir.ornix.passgen.feature.home.impl.presentation

internal fun reduce(
    oldState: HomeUiState,
    change: HomePartialState
): HomeUiState {
    return when (change) {
        is HomePartialState.InputChanged -> {
            oldState.copy(input = change.input)
        }

        is HomePartialState.Loading -> {
            oldState.copy(isLoading = false)
        }

        is HomePartialState.PasswordItemsLoaded -> {
            oldState.copy(
                passGenItems = change.passGenItems
            )
        }

        is HomePartialState.PasswordGenerated -> {
            oldState.copy(
                passGenItems = oldState.passGenItems.map { passwordItem ->
                    if (passwordItem.config.id == change.configId)
                        passwordItem.copy(
                            password = change.password,
                            isCalculating = false
                        )
                    else passwordItem
                }
            )
        }

        is HomePartialState.PasswordIsCalculating -> {
            oldState.copy(
                passGenItems = oldState.passGenItems.map { passwordItem ->
                    if (passwordItem.config.id == change.configId)
                        passwordItem.copy(
                            password = null,
                            isCalculating = true
                        )
                    else passwordItem
                }
            )
        }
    }
}
