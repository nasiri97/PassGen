package ir.ornix.passgen.passwordgenerator

import ir.ornix.passgen.codec.Utf8TextCodec
import ir.ornix.passgen.passwordgenerator.model.KDFPassGenConfig
import ir.ornix.passgen.passwordgenerator.model.PassGenFeed
import ir.ornix.passgen.passwordgenerator.model.Password
import ir.ornix.passgen.passwordgenerator.model.PreprocessConfig.Companion.SINGLE_SPACE

data class KDFPassGen(override val passGenConfig: KDFPassGenConfig) : PassGen {

    private val tokenGen by lazy {
        TokenGen(
            hashing = passGenConfig.hashingType.createInstance(),
            inputDecoder = Utf8TextCodec(),
            outputEncoder = passGenConfig.encoderType.createInstance()
        )
    }

    init {
        validatePasswordLength(passGenConfig.postProcessConfig.passwordLength)
    }

    suspend fun generate(feed: PassGenFeed): Password? {
        val normalizedInput =
            feed.masterKey + SINGLE_SPACE + passGenConfig.preprocessConfig(feed.input)
        val token =
            tokenGen.getToken(normalizedInput)
        return passGenConfig.postProcessConfig(token = token)
    }


    /**
     * Validates that the requested password length is within allowed bounds
     * and does not exceed the size of the generated token.
     *
     * @param passLength The requested length of the password.
     * @throws IllegalArgumentException if the password length is invalid or too large for the token.
     */
    private fun validatePasswordLength(passLength: Int) {
        require(tokenGen.tokenLength >= passLength) {
            "Requested Password length must not exceed ${tokenGen.tokenLength}, which is the length of the Token!"
        }
    }
}