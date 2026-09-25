package ir.ornix.passgen.core.domain.passgenconfig.model

import ir.ornix.passgen.core.common.passwordgenerator.kdf.KDFPassGen
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import kotlinx.serialization.Serializable

@Serializable
data class KDFPassGenConfig(
    override val id: Int,
    override val name: String,
    val preprocessConfig: PreprocessConfig,
    val inputHasher: InputHasher,
    override val passEncoder: PassEncoder,
    override val passwordLength: Int?,
) : PassGenConfig {

    init {
        validatePasswordLength()
    }

    override val typeBrief =
        "${inputHasher.key}-${passEncoder.key}" +
                when (passEncoder) {
                    is SeedPassEncoder -> ""
                    is StringPassEncoder -> "-${passwordLength}"
                }


    override fun createPassGen(): KDFPassGen {
        return KDFPassGen(
            inputHasher = inputHasher,
            passEncoder = passEncoder,
            passwordLength = passwordLength
        )
    }

}