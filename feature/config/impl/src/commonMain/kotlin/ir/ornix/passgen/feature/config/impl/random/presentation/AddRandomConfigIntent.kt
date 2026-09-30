package ir.ornix.passgen.feature.config.impl.random.presentation

import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder

sealed interface AddRandomConfigIntent {
    data class NameChanged(val name: String) : AddRandomConfigIntent
    data class EncoderSelected(val encoder: PassEncoder) : AddRandomConfigIntent
    data class PassLengthChanged(val length: Int) : AddRandomConfigIntent
    data object SubmitConfigClicked : AddRandomConfigIntent
}
