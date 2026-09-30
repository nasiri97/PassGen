package ir.ornix.passgen.core.model.passgenconfig

import ir.ornix.passgen.core.common.passwordgenerator.core.PassGen
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.random.RandomPassGen
import ir.ornix.passgen.core.model.PasswordStrengthLevel
import ir.ornix.passgen.core.model.PasswordStrengthLevel.Companion.entropyByteSizeToPasswordStrengthLevel
import kotlinx.serialization.Serializable


@Serializable
sealed interface PassGenConfig {
    val id: Int
    val name: String
    val passEncoder: PassEncoder
    val passwordLength: Int?
    val typeBrief: String

    fun createPassGen(): PassGen


    val entropyByteSize: Int
        get() = when (passEncoder) {
            is SeedPassEncoder ->
                (passEncoder as SeedPassEncoder).entropyByteSize

            is StringPassEncoder ->
                (passEncoder as StringPassEncoder).getEntropyByteSize(passwordLength!!)
        }


    val strengthLevel: PasswordStrengthLevel
        get() = entropyByteSizeToPasswordStrengthLevel(entropyByteSize)

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