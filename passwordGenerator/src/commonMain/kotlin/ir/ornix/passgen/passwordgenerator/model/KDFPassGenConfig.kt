package ir.ornix.passgen.passwordgenerator.model

import kotlinx.serialization.Serializable

@Serializable
data class KDFPassGenConfig(
    override val id: Int,
    override val name: String,
    val preprocessConfig: PreprocessConfig,
    val hashingType: HashingType,
    val encoderType: EncoderType,
    val postProcessConfig: PostProcessConfig
) : PassGenConfig {

    override val passwordLength by lazy {
        postProcessConfig.passwordLength
    }

    override val typeBrief by lazy {
        "${hashingType.key}-${encoderType.key}-${postProcessConfig.passwordLength}"
    }

    companion object {
        val sampleKDFPassGenConfig = KDFPassGenConfig(
            id = 0,
            name = "Test",
            preprocessConfig = PreprocessConfig(
                trimLeadingAndTrailingSpaces = true,
                collapseMultipleSpaces = true,
                convertToLowercase = false
            ),
            hashingType = HashingType.ARGON2ID,
            encoderType = EncoderType.BASE64,
            postProcessConfig = PostProcessConfig(passwordLength = 64)
        )
    }
}