package ir.ornix.passgen.feature.config.impl.random.presentation

import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder

sealed interface AddRandomConfigPartialState {
    data class NameUpdated(val name: String) : AddRandomConfigPartialState
    data class EncoderUpdated(val encoder: PassEncoder) : AddRandomConfigPartialState
    data class PassLengthUpdated(val length: Int) : AddRandomConfigPartialState
    data object Submitting : AddRandomConfigPartialState
    data object ConfigCreatedSuccess : AddRandomConfigPartialState
    data class ConfigCreatedError(val message: String) : AddRandomConfigPartialState
}
