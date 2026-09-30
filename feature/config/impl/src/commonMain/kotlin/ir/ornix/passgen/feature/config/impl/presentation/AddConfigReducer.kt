package ir.ornix.passgen.feature.config.impl.presentation

internal fun reduce(
    oldState: AddConfigUiState,
    change: AddConfigPartialState
): AddConfigUiState {
    return when (change) {
        is AddConfigPartialState.StepChanged -> oldState.copy(step = change.step)
        is AddConfigPartialState.MasterKeyUpdated -> oldState.copy(masterKey = change.masterKey)
        is AddConfigPartialState.ConfirmMasterKeyUpdated -> oldState.copy(confirmMasterKey = change.confirmMasterKey)
        is AddConfigPartialState.NameUpdated -> oldState.copy(name = change.name)
        is AddConfigPartialState.TrimSpacesUpdated -> oldState.copy(trimSpaces = change.enabled)
        is AddConfigPartialState.CollapseSpacesUpdated -> oldState.copy(collapseSpaces = change.enabled)
        is AddConfigPartialState.LowercaseUpdated -> oldState.copy(lowercase = change.enabled)
        is AddConfigPartialState.HasherUpdated -> {
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
        is AddConfigPartialState.EncoderUpdated -> oldState.copy(selectedEncoder = change.encoder)
        is AddConfigPartialState.PassLengthUpdated -> oldState.copy(passLength = change.length)
        is AddConfigPartialState.Submitting -> oldState.copy(isSubmitting = true, errorMessage = null)
        is AddConfigPartialState.ConfigCreatedSuccess -> oldState.copy(isSubmitting = false, isSuccess = true)
        is AddConfigPartialState.ConfigCreatedError -> oldState.copy(isSubmitting = false, errorMessage = change.message)
    }
}
