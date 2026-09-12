package ir.ornix.passgen.core.domain.core

import ir.ornix.passgen.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.passwordgenerator.random.RandomPassGen
import kotlinx.serialization.Serializable

@Serializable
data class RandomPassGenConfig(
    override val id: Int,
    override val name: String,
    override val passEncoder: PassEncoder,
    override val passwordLength: Int
) : PassGenConfig {

    override val typeBrief = "RANDOM-${passEncoder.key}-${passwordLength}"

    override fun createPassGen(): RandomPassGen {
        return RandomPassGen(
            passEncoder = passEncoder,
            passwordLength = passwordLength
        )
    }

}