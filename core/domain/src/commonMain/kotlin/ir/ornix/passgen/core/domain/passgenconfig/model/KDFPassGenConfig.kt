package ir.ornix.passgen.core.domain.passgenconfig.model

import ir.ornix.passgen.passwordgenerator.kdf.KDFPassGen
import ir.ornix.passgen.passwordgenerator.model.InputHasher
import ir.ornix.passgen.passwordgenerator.model.PassEncoder
import kotlinx.serialization.Serializable

@Serializable
data class KDFPassGenConfig(
    override val id: Int,
    override val name: String,
    val preprocessConfig: PreprocessConfig,
    val inputHasher: InputHasher,
    override val passEncoder: PassEncoder,
    override val passwordLength: Int,
) : PassGenConfig {

    override val typeBrief = "${inputHasher.key}-${passEncoder.key}-${passwordLength}"

    override fun createPassGen(): KDFPassGen {
        return KDFPassGen(
            inputHasher = inputHasher,
            passEncoder = passEncoder,
            passwordLength = passwordLength
        )
    }

}