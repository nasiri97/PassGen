package ir.ornix.passgen.feature.config.impl.random.presentation

import ir.ornix.passgen.feature.config.impl.util.getEntropySize
import ir.ornix.passgen.feature.config.impl.util.getRandomMaxAvailablePassLength
import ir.ornix.passgen.feature.config.impl.util.getValidPassLength

internal fun reduce(
    oldState: AddRandomConfigUiState,
    change: AddRandomConfigPartialState
): AddRandomConfigUiState {
    return when (change) {
        is AddRandomConfigPartialState.NameUpdated -> oldState.copy(name = change.name)

        is AddRandomConfigPartialState.EncoderUpdated -> {
            val newEncoder = change.encoder

            val newMaxAvailablePassLength = getRandomMaxAvailablePassLength(
                encoder = newEncoder
            )

            val newPassLength = getValidPassLength(
                encoder = newEncoder,
                maxAvailablePassLength = newMaxAvailablePassLength,
                currentPassLength = oldState.passLength
            )

            val newEntropyByteSize = getEntropySize(
                encoder = newEncoder,
                passLength = newPassLength
            )

            oldState.copy(
                selectedEncoder = newEncoder,
                maxAvailablePassLength = newMaxAvailablePassLength,
                passLength = newPassLength,
                entropyByteSize = newEntropyByteSize
            )
        }

        is AddRandomConfigPartialState.PassLengthUpdated -> {
            val newPassLength = change.length

            val newEntropyByteSize = getEntropySize(
                encoder = oldState.selectedEncoder,
                passLength = newPassLength
            )

            oldState.copy(
                passLength = newPassLength,
                entropyByteSize = newEntropyByteSize
            )
        }

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