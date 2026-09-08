package ir.ornix.passgen.passwordgenerator.random

import ir.ornix.passgen.passwordgenerator.core.PassGenConfig
import ir.ornix.passgen.passwordgenerator.model.PassEncoder
import kotlinx.serialization.Serializable

@Serializable
data class RandomPassGenConfig(
    override val id: Int,
    override val name: String,
    override val passEncoder: PassEncoder,
    override val passwordLength: Int
) : PassGenConfig {
    override val typeBrief = "RANDOM-${passEncoder.key}-${passwordLength}"
}