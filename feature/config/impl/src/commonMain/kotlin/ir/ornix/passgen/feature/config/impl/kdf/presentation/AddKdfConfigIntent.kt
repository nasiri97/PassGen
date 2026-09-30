package ir.ornix.passgen.feature.config.impl.kdf.presentation

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder

sealed interface AddKdfConfigIntent {
    data class MasterKeyChanged(val masterKey: String) : AddKdfConfigIntent
    data class ConfirmMasterKeyChanged(val confirmMasterKey: String) : AddKdfConfigIntent
    data object NextStepClicked : AddKdfConfigIntent
    data object PreviousStepClicked : AddKdfConfigIntent

    data class NameChanged(val name: String) : AddKdfConfigIntent
    data class TrimSpacesToggled(val enabled: Boolean) : AddKdfConfigIntent
    data class CollapseSpacesToggled(val enabled: Boolean) : AddKdfConfigIntent
    data class LowercaseToggled(val enabled: Boolean) : AddKdfConfigIntent

    data class HasherSelected(val hasher: InputHasher) : AddKdfConfigIntent
    data class EncoderSelected(val encoder: PassEncoder) : AddKdfConfigIntent
    data class PassLengthChanged(val length: Int) : AddKdfConfigIntent

    data object SubmitConfigClicked : AddKdfConfigIntent
}
