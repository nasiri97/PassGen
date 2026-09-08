package ir.ornix.passgen.passwordgenerator.kdf

import ir.ornix.passgen.codec.Utf8TextCodec
import ir.ornix.passgen.passwordgenerator.core.PassGen
import ir.ornix.passgen.passwordgenerator.model.PreprocessConfig

data class KDFPassGen(override val passGenConfig: KDFPassGenConfig) : PassGen {

    private val tokenGen = TokenGen(
        hashing = passGenConfig.hashingType.createInstance(),
        inputDecoder = Utf8TextCodec(),
        outputPassEncoder = passGenConfig.passEncoder
    )


    suspend fun generate(feed: PassGenFeed): String? {
        val normalizedInput =
            feed.masterKey + PreprocessConfig.SINGLE_SPACE + passGenConfig.preprocessConfig(feed.input)
        val token = tokenGen.getToken(normalizedInput)
        return token?.substring(0, passGenConfig.passwordLength)
    }
}