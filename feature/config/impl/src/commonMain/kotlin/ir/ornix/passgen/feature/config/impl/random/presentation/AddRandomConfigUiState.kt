package ir.ornix.passgen.feature.config.impl.random.presentation

import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder


data class AddRandomConfigUiState(
    val name: String = "",
    val selectedEncoder: PassEncoder = StringPassEncoder.Z85PassEncoder,
    val passLength: Int = 64,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
