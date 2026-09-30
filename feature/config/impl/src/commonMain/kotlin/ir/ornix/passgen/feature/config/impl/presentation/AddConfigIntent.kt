package ir.ornix.passgen.feature.config.impl.presentation

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder

sealed interface AddConfigIntent {
    data class MasterKeyChanged(val masterKey: String) : AddConfigIntent
    data class ConfirmMasterKeyChanged(val confirmMasterKey: String) : AddConfigIntent
    data object NextStepClicked : AddConfigIntent
    data object PreviousStepClicked : AddConfigIntent

    data class NameChanged(val name: String) : AddConfigIntent
    data class TrimSpacesToggled(val enabled: Boolean) : AddConfigIntent
    data class CollapseSpacesToggled(val enabled: Boolean) : AddConfigIntent
    data class LowercaseToggled(val enabled: Boolean) : AddConfigIntent

    data class HasherSelected(val hasher: InputHasher) : AddConfigIntent
    data class EncoderSelected(val encoder: PassEncoder) : AddConfigIntent
    data class PassLengthChanged(val length: Int) : AddConfigIntent

    data object SubmitConfigClicked : AddConfigIntent
}
