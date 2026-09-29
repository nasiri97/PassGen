package ir.ornix.passgen.feature.home.impl.presentation

import ir.ornix.passgen.core.model.passgenconfig.KDFPassGenConfig
import ir.ornix.passgen.core.model.Password

data class PasswordItem(
    val config: KDFPassGenConfig,
    val password: Password?,
    val isCalculating: Boolean = true
)