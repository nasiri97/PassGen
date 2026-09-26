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

        is HomePartialState.ShowAddConfigDialog -> {
            oldState.copy(isAddConfigDialogVisible = true)
        }

        is HomePartialState.HideAddConfigDialog, HomePartialState.ConfigCreated -> {
            oldState.copy(isAddConfigDialogVisible = false)
        }

        is HomePartialState.PasswordItemsLoaded -> {
            oldState.copy(
                passwordItems = change.passwordItems
            )
        }

        is HomePartialState.PasswordGenerated -> {
            oldState.copy(
                passwordItems = oldState.passwordItems.map { passwordItem ->
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
                passwordItems = oldState.passwordItems.map { passwordItem ->
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