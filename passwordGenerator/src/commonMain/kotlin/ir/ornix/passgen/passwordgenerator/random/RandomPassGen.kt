package ir.ornix.passgen.passwordgenerator.random

import ir.ornix.passgen.passwordgenerator.core.PassGen
import ir.ornix.passgen.passwordgenerator.model.PassEncoder

data class RandomPassGen(
    override val passEncoder: PassEncoder,
    override val passwordLength: Int
) : PassGen {

    init {
        validatePasswordLength()
    }

    fun generate(): String {
        return passEncoder.encode(secureRandomBytes(passwordLength))
    }

    /**
     * Validates that the requested password length is within allowed bounds.
     *
     * @throws IllegalArgumentException if the password length is invalid.
     */
    private fun validatePasswordLength() {
        require(passwordLength >= 4) { "Password length must be at least 4 characters!" }
        require(passwordLength <= 256) { "Password length must be at most 256 characters!" }
    }

}