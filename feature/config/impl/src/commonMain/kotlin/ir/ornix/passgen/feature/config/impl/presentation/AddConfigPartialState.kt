package ir.ornix.passgen.feature.config.impl.presentation

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder

sealed interface AddConfigPartialState {
    data class StepChanged(val step: AddConfigStep) : AddConfigPartialState
    data class MasterKeyUpdated(val masterKey: String) : AddConfigPartialState
    data class ConfirmMasterKeyUpdated(val confirmMasterKey: String) : AddConfigPartialState

    data class NameUpdated(val name: String) : AddConfigPartialState
    data class TrimSpacesUpdated(val enabled: Boolean) : AddConfigPartialState
    data class CollapseSpacesUpdated(val enabled: Boolean) : AddConfigPartialState
    data class LowercaseUpdated(val enabled: Boolean) : AddConfigPartialState

    data class HasherUpdated(val hasher: InputHasher, val validEncoders: List<PassEncoder>) : AddConfigPartialState
    data class EncoderUpdated(val encoder: PassEncoder) : AddConfigPartialState
    data class PassLengthUpdated(val length: Int) : AddConfigPartialState

    data object Submitting : AddConfigPartialState
    data object ConfigCreatedSuccess : AddConfigPartialState
    data class ConfigCreatedError(val message: String) : AddConfigPartialState
}
