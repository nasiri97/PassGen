package ir.ornix.passgen.feature.config.impl.random.presentation

import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.random.RandomPassGen
import ir.ornix.passgen.feature.config.impl.util.DEFAULT_PASS_ENCODER
import ir.ornix.passgen.feature.config.impl.util.DEFAULT_PASS_LENGTH
import ir.ornix.passgen.feature.config.impl.util.getEntropySize
import ir.ornix.passgen.feature.config.impl.util.getRandomMaxAvailablePassLength
import ir.ornix.passgen.feature.config.impl.util.getValidPassLength


data class AddRandomConfigUiState(
    val name: String = "",
    val availableEncoders: List<PassEncoder> = PassEncoder.getValidItems(RandomPassGen.RANDOM_PASS_GEN_MAX_ENTROPY_BYTE_SIZE),
    val selectedEncoder: PassEncoder = DEFAULT_PASS_ENCODER,
    val maxAvailablePassLength: Int? = getRandomMaxAvailablePassLength(selectedEncoder),
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
