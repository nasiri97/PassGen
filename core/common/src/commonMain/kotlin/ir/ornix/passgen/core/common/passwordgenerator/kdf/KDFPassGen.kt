package ir.ornix.passgen.core.common.passwordgenerator.kdf

import ir.ornix.passgen.core.common.codec.core.Decoder
import ir.ornix.passgen.core.common.passwordgenerator.core.PassGen
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder

data class KDFPassGen(
    val inputHasher: InputHasher,
    override val passEncoder: PassEncoder,
    override val passwordLength: Int?
) : PassGen {

    init {
        validatePasswordLength()
    }

    private val tokenGen = TokenGen(
        inputHasher = inputHasher,
        outputPassEncoder = passEncoder
    )

    suspend fun generate(input: String, inputDecoder: Decoder): String? {
        return generate(inputDecoder.decode(input))
    }

    suspend fun generate(input: ByteArray): String? {
        val token = tokenGen.getToken(input)

        return when (passEncoder) {
            is SeedPassEncoder -> token
            is StringPassEncoder -> token?.substring(0, passwordLength!!)
        }
    }

    /**
     * Validates that the requested password length is within allowed bounds
     * and does not exceed the size of the generated token.
     *
     * @throws IllegalArgumentException if the password length is invalid or too large for the token.
     */
    private fun validatePasswordLength() {
        when (passEncoder) {
            is SeedPassEncoder -> {
                require(passwordLength == null) {
                    "Password must be null for SeedPassEncoder!"
                }
            }

            is StringPassEncoder -> {
                val tokenLength = passEncoder.getTokenLength(inputHasher)
                require(passwordLength != null) {
                    "Password must not be null for StringPassEncoder!"
                }

                require(passwordLength > 0) { "Password length must be a positive number (greater than 0)!" }
                require(passwordLength <= tokenLength) {
                    "Requested Password length must not exceed $tokenLength, which is the length of the Token!"
                }
            }
        }
    }
}