package ir.ornix.passgen.passwordgenerator.kdf

import ir.ornix.passgen.codec.core.Decoder
import ir.ornix.passgen.passwordgenerator.core.PassGen
import ir.ornix.passgen.passwordgenerator.model.InputHasher
import ir.ornix.passgen.passwordgenerator.model.PassEncoder

data class KDFPassGen(
    val inputHasher: InputHasher,
    override val passEncoder: PassEncoder,
    override val passwordLength: Int
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
        return token?.substring(0, passwordLength)
    }

    /**
     * Validates that the requested password length is within allowed bounds
     * and does not exceed the size of the generated token.
     *
     * @throws IllegalArgumentException if the password length is invalid or too large for the token.
     */
    private fun validatePasswordLength() {
        val tokenLength = passEncoder.getTokenLength(inputHasher)

        require(passwordLength >= 4) { "Password length must be at least 4 characters!" }
        require(passwordLength <= tokenLength) {
            "Requested Password length must not exceed $tokenLength, which is the length of the Token!"
        }
    }
}