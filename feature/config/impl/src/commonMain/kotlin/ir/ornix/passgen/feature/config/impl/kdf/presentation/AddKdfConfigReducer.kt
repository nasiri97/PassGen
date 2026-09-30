package ir.ornix.passgen.feature.config.impl.kdf.presentation

import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.feature.config.impl.util.getEntropySize
import ir.ornix.passgen.feature.config.impl.util.getKdfMaxAvailablePassLength
import ir.ornix.passgen.feature.config.impl.util.getValidPassLength

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

            val newHasher = change.hasher

            val newAvailableEncoders = PassEncoder.getValidItems(
                inputHasher = newHasher
            )

            val newEncoder = if (newAvailableEncoders.contains(oldState.selectedEncoder))
                oldState.selectedEncoder
            else
                newAvailableEncoders.first()

            val newMaxAvailablePassLength = getKdfMaxAvailablePassLength(
                hasher = newHasher,
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
                selectedHasher = newHasher,
                availableEncoders = newAvailableEncoders,
                selectedEncoder = newEncoder,
                maxAvailablePassLength = newMaxAvailablePassLength,
                passLength = newPassLength,
                entropyByteSize = newEntropyByteSize
            )
        }

        is AddKdfConfigPartialState.EncoderUpdated -> {

            val newEncoder = change.encoder

            val newMaxAvailablePassLength = getKdfMaxAvailablePassLength(
                hasher = oldState.selectedHasher,
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

        is AddKdfConfigPartialState.PassLengthUpdated -> {
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