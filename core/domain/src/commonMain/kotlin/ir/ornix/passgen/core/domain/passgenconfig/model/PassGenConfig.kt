package ir.ornix.passgen.core.domain.passgenconfig.model

import ir.ornix.passgen.core.common.passwordgenerator.core.PassGen
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder


interface PassGenConfig {
    val id: Int
    val name: String
    val passEncoder: PassEncoder
    val passwordLength: Int?
    val typeBrief: String

    fun createPassGen(): PassGen


    /**
     * Validates that the requested password length is within allowed bounds
     *
     * @throws IllegalArgumentException if the password length is invalid
     */
    fun validatePasswordLength() {
        when (passEncoder) {
            is SeedPassEncoder -> {
                require(passwordLength == null) {
                    "Password must be null for SeedPassEncoder!"
                }
            }

            is StringPassEncoder -> {
                require(passwordLength != null) {
                    "Password must not be null for StringPassEncoder!"
                }

                passwordLength?.let { passwordLength ->
                    require(passwordLength >= 4) { "Password length must be at least 4 characters!" }
                    require(passwordLength <= 64) { "Password length must be at most 64 characters!" }
                }
            }
        }
    }
}