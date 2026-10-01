package ir.ornix.passgen.core.model

import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig

data class PassGenItem(
    val config: PassGenConfig,
    val password: Password?,
    val isCalculating: Boolean
)