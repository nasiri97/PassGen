package ir.ornix.passgen.core.domain.passgenconfig.model

import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.random.RandomPassGen
import kotlinx.serialization.Serializable

@Serializable
data class RandomPassGenConfig(
    override val id: Int,
    override val name: String,
    override val passEncoder: PassEncoder,
    override val passwordLength: Int
) : PassGenConfig {

    init {
        validatePasswordLength()
    }

    override val typeBrief = "RANDOM-${passEncoder.key}" +
            when (passEncoder) {
                is SeedPassEncoder -> ""
                is StringPassEncoder -> "-${passwordLength}"
            }

    override fun createPassGen(): RandomPassGen {
        return RandomPassGen(
            passEncoder = passEncoder,
            passwordLength = passwordLength
        )
    }

}