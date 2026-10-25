package ir.ornix.passgen.feature.config.impl.kdf.presentation

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder

internal sealed interface AddKdfConfigIntent {
    data class MasterKeyChanged(val masterKey: CharArray) : AddKdfConfigIntent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is MasterKeyChanged) return false
            return masterKey.contentEquals(other.masterKey)
        }

        override fun hashCode(): Int {
            return masterKey.contentHashCode()
        }
    }

    data class ConfirmMasterKeyChanged(val confirmMasterKey: CharArray) : AddKdfConfigIntent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ConfirmMasterKeyChanged) return false
            return confirmMasterKey.contentEquals(other.confirmMasterKey)
        }

        override fun hashCode(): Int {
            return confirmMasterKey.contentHashCode()
        }
    }

    data object NextStepClicked : AddKdfConfigIntent
    data object PreviousStepClicked : AddKdfConfigIntent
    data object ProcessCancelled : AddKdfConfigIntent
    data class NameChanged(val name: String) : AddKdfConfigIntent
    data class TrimSpacesToggled(val enabled: Boolean) : AddKdfConfigIntent
    data class CollapseSpacesToggled(val enabled: Boolean) : AddKdfConfigIntent
    data class LowercaseToggled(val enabled: Boolean) : AddKdfConfigIntent

    data class HasherSelected(val hasher: InputHasher) : AddKdfConfigIntent
    data class EncoderSelected(val encoder: PassEncoder) : AddKdfConfigIntent
    data class PassLengthChanged(val length: Int) : AddKdfConfigIntent

    data object SubmitConfigClicked : AddKdfConfigIntent
}
