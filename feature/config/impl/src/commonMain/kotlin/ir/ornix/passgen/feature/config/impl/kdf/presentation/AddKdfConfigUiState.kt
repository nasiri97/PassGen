package ir.ornix.passgen.feature.config.impl.kdf.presentation

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.feature.config.impl.util.DEFAULT_INPUT_HASHER
import ir.ornix.passgen.feature.config.impl.util.DEFAULT_PASS_ENCODER
import ir.ornix.passgen.feature.config.impl.util.DEFAULT_PASS_LENGTH
import ir.ornix.passgen.feature.config.impl.util.getEntropySize
import ir.ornix.passgen.feature.config.impl.util.getKdfMaxAvailablePassLength
import ir.ornix.passgen.feature.config.impl.util.getValidPassLength

enum class AddKdfConfigStep {
    MasterKey,
    ConfigDetails
}

data class AddKdsConfigUiState(
    val step: AddKdfConfigStep = AddKdfConfigStep.MasterKey,
    val isProcessCancelled: Boolean = false,
    val name: String = "",
    val trimSpaces: Boolean = true,
    val collapseSpaces: Boolean = true,
    val lowercase: Boolean = true,
    val selectedHasher: InputHasher = DEFAULT_INPUT_HASHER,
    val availableEncoders: List<PassEncoder> = PassEncoder.getValidItems(selectedHasher),
    val selectedEncoder: PassEncoder = DEFAULT_PASS_ENCODER,
    val maxAvailablePassLength: Int? = getKdfMaxAvailablePassLength(
        hasher = selectedHasher,
        encoder = selectedEncoder
    ),
    val passLength: Int = getValidPassLength(
        encoder = selectedEncoder,
        maxAvailablePassLength = maxAvailablePassLength,
        currentPassLength = DEFAULT_PASS_LENGTH
    ),
    val entropyByteSize: Int = getEntropySize(
        encoder = selectedEncoder,
        passLength = passLength
    ),
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
