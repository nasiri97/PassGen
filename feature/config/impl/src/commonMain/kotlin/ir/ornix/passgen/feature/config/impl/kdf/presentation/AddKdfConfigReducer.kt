package ir.ornix.passgen.feature.config.impl.kdf.presentation

internal fun reduce(
    oldState: AddKdsConfigUiState,
    change: AddKdfConfigPartialState
): AddKdsConfigUiState {
    return when (change) {
        is AddKdfConfigPartialState.StepChanged -> oldState.copy(step = change.step)
        is AddKdfConfigPartialState.MasterKeyUpdated -> oldState.copy(masterKey = change.masterKey)
        is AddKdfConfigPartialState.ConfirmMasterKeyUpdated -> oldState.copy(confirmMasterKey = change.confirmMasterKey)
        is AddKdfConfigPartialState.NameUpdated -> oldState.copy(name = change.name)
        is AddKdfConfigPartialState.TrimSpacesUpdated -> oldState.copy(trimSpaces = change.enabled)
        is AddKdfConfigPartialState.CollapseSpacesUpdated -> oldState.copy(collapseSpaces = change.enabled)
        is AddKdfConfigPartialState.LowercaseUpdated -> oldState.copy(lowercase = change.enabled)
        is AddKdfConfigPartialState.HasherUpdated -> {
            val newEncoder = if (change.validEncoders.contains(oldState.selectedEncoder)) {
                oldState.selectedEncoder
            } else {
                change.validEncoders.first()
            }
            oldState.copy(
                selectedHasher = change.hasher,
                selectedEncoder = newEncoder
            )
        }

        is AddKdfConfigPartialState.EncoderUpdated -> oldState.copy(selectedEncoder = change.encoder)
        is AddKdfConfigPartialState.PassLengthUpdated -> oldState.copy(passLength = change.length)
        is AddKdfConfigPartialState.Submitting -> oldState.copy(
            isSubmitting = true,
            errorMessage = null
        )

        is AddKdfConfigPartialState.ConfigCreatedSuccess -> oldState.copy(
            isSubmitting = false,
            isSuccess = true
        )

        is AddKdfConfigPartialState.ConfigCreatedError -> oldState.copy(
            isSubmitting = false,
            errorMessage = change.message
        )
    }
}
