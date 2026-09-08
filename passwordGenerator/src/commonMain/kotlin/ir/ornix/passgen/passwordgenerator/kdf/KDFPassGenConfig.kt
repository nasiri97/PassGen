package ir.ornix.passgen.passwordgenerator.kdf

import ir.ornix.passgen.passwordgenerator.core.PassGenConfig
import ir.ornix.passgen.passwordgenerator.model.HashingType
import ir.ornix.passgen.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.passwordgenerator.model.PreprocessConfig
import kotlinx.serialization.Serializable

@Serializable
data class KDFPassGenConfig(
    override val id: Int,
    override val name: String,
    override val passEncoder: PassEncoder,
    override val passwordLength: Int,
    val preprocessConfig: PreprocessConfig,
    val hashingType: HashingType
) : PassGenConfig {

    override val typeBrief = "${hashingType.key}-${passEncoder.key}-${passwordLength}"

    init {
        validatePasswordLength()
    }

    /**
     * Validates that the requested password length is within allowed bounds
     * and does not exceed the size of the generated token.
     *
     * @throws IllegalArgumentException if the password length is invalid or too large for the token.
     */
    private fun validatePasswordLength() {
        val tokenLength = passEncoder.getTokenLength(hashingType.createInstance())
        require(tokenLength >= passwordLength) {
            "Requested Password length must not exceed $tokenLength, which is the length of the Token!"
        }
    }
}