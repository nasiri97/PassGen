package ir.ornix.passgen.feature.config.impl.random.presentation

internal fun reduce(
    oldState: AddRandomConfigUiState,
    change: AddRandomConfigPartialState
): AddRandomConfigUiState {
    return when (change) {
        is AddRandomConfigPartialState.NameUpdated -> oldState.copy(name = change.name)
        is AddRandomConfigPartialState.EncoderUpdated -> oldState.copy(selectedEncoder = change.encoder)
        is AddRandomConfigPartialState.PassLengthUpdated -> oldState.copy(passLength = change.length)
        is AddRandomConfigPartialState.Submitting -> oldState.copy(
            isSubmitting = true,
            errorMessage = null
        )

        is AddRandomConfigPartialState.ConfigCreatedSuccess -> oldState.copy(
            isSubmitting = false,
            isSuccess = true
        )

        is AddRandomConfigPartialState.ConfigCreatedError -> oldState.copy(
            isSubmitting = false,
            errorMessage = change.message
        )
    }
}
