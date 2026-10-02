package ir.ornix.passgen.feature.config.impl.kdf.presentation

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder

sealed interface AddKdfConfigPartialState {
    data class StepChanged(val step: AddKdfConfigStep) : AddKdfConfigPartialState
    data object ProcessCancelled : AddKdfConfigPartialState

    data class NameUpdated(val name: String) : AddKdfConfigPartialState
    data class TrimSpacesUpdated(val enabled: Boolean) : AddKdfConfigPartialState
    data class CollapseSpacesUpdated(val enabled: Boolean) : AddKdfConfigPartialState
    data class LowercaseUpdated(val enabled: Boolean) : AddKdfConfigPartialState

    data class HasherUpdated(val hasher: InputHasher, val validEncoders: List<PassEncoder>) :
        AddKdfConfigPartialState

    data class EncoderUpdated(val encoder: PassEncoder) : AddKdfConfigPartialState
    data class PassLengthUpdated(val length: Int) : AddKdfConfigPartialState

    data object Submitting : AddKdfConfigPartialState
    data object ConfigCreatedSuccess : AddKdfConfigPartialState
    data class ConfigCreatedError(val message: String) : AddKdfConfigPartialState
}
