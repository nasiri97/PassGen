package ir.ornix.passgen.core.model.passgenconfig

import ir.ornix.passgen.core.common.passwordgenerator.kdf.KDFPassGen
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("kdf_pass_gen_config")
data class KdfPassGenConfig(
    override val id: Int,
    override val name: String,
    val preprocessConfig: PreprocessConfig,
    val inputHasher: InputHasher,
    override val passEncoder: PassEncoder,
    override val passwordLength: Int?
) : PassGenConfig {

    init {
        validatePasswordLength()
    }

    override val typeBrief =
        "${inputHasher.shortName}-${passEncoder.shortName}" +
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