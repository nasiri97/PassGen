package ir.ornix.passgen.core.domain.passgenconfig.model

import ir.ornix.passgen.core.common.passwordgenerator.core.PassGen
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.random.RandomPassGen


interface PassGenConfig {
    val id: Int
    val name: String
    val passEncoder: PassEncoder
    val passwordLength: Int?
    val typeBrief: String

    fun createPassGen(): PassGen


    /**
     * Validates the password-length configuration for the selected encoder.
     *
     * @throws IllegalArgumentException if the configuration is invalid.
     */
    fun validatePasswordLength() {
        PassGen.validatePasswordLength(
            passEncoder = passEncoder,
            maxEntropyByteSize = when (this) {
                is KDFPassGenConfig -> this.inputHasher.outputByteSize
                is RandomPassGenConfig -> RandomPassGen.RANDOM_PASS_GEN_MAX_ENTROPY_BYTE_SIZE
                else -> 0
            },
            passwordLength = passwordLength
        )
    }
}