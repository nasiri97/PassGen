package ir.ornix.passgen.feature.config.impl.presentation

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder

enum class AddConfigStep {
    MasterKey,
    ConfigDetails
}

data class AddConfigUiState(
    val step: AddConfigStep = AddConfigStep.MasterKey,
    val masterKey: String = "",
    val confirmMasterKey: String = "",
    val name: String = "",
    val trimSpaces: Boolean = true,
    val collapseSpaces: Boolean = true,
    val lowercase: Boolean = true,
    val selectedHasher: InputHasher = InputHasher.ARGON2ID,
    val selectedEncoder: PassEncoder = StringPassEncoder.Z85PassEncoder,
    val passLength: Int = 64,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
