package ir.ornix.passgen.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: Int = 0,
    val username: String,
    val password: String
)
