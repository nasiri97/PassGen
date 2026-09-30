package ir.ornix.passgen.feature.home.impl.presentation

import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.Password

data class PasswordItem(
    val config: KdfPassGenConfig,
    val password: Password?,
    val isCalculating: Boolean = true
)