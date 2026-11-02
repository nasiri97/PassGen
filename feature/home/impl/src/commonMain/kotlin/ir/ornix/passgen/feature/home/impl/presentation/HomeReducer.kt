package ir.ornix.passgen.feature.home.impl.presentation

internal fun reduce(
    oldState: HomeUiState,
    change: HomePartialState
): HomeUiState {
    return when (change) {
        is HomePartialState.ContextInputChanged -> {
            oldState.copy(
                contextInput = change.contextInput,
                passGenItems = oldState.passGenItems.map { passGenItem ->
                    if (passGenItem.contextInput == change.contextInput) passGenItem
                    else passGenItem.copy(
                        contextInput = change.contextInput,
                        password = null,
                        isCalculating = true
                    )
                }
            )
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
                passGenItems = oldState.passGenItems.map { passGenItem ->
                    if (passGenItem.config.id == change.configId)
                        passGenItem.copy(
                            password = change.password,
                            isCalculating = false
                        )
                    else passGenItem
                }
            )
        }

        is HomePartialState.PasswordIsCalculating -> {
            oldState.copy(
                passGenItems = oldState.passGenItems.map { passGenItem ->
                    if (passGenItem.config.id == change.configId)
                        passGenItem.copy(
                            password = null,
                            isCalculating = true
                        )
                    else passGenItem
                }
            )
        }

        is HomePartialState.CopyEnabledChanged ->
            oldState.copy(isCopyEnabled = change.isCopyEnabled)

        is HomePartialState.PasswordVisibilityTimeoutChanged ->
            oldState.copy(visibilityTimeout = change.visibilityTimeout)
    }
}
