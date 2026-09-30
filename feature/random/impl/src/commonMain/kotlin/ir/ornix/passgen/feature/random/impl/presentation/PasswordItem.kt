package ir.ornix.passgen.feature.random.impl.presentation

import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig

data class PasswordItem(
    val config: RandomPassGenConfig,
    val password: Password?
)