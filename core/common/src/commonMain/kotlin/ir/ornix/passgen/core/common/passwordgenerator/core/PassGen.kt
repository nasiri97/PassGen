package ir.ornix.passgen.core.common.passwordgenerator.core

import ir.ornix.passgen.core.common.passwordgenerator.model.IllegalPasswordLengthException
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder


/**
 * Defines a password generator that produces entropy bytes and transforms
 * them into a final password using a [PassEncoder].
 *
 * The maximum entropy size is determined by the underlying entropy-generation
 * mechanism and is independent of both [passEncoder] and [passwordLength].
 *
 * The generated entropy bytes are first encoded by [passEncoder]. For
 * [StringPassEncoder], [passwordLength] is then used to select the requested
 * portion of the encoded result. [SeedPassEncoder] returns the complete
 * encoded result.
 */
interface PassGen {

    /**
     * Encoder used to transform the generated entropy bytes into the final
     * password representation.
     */
    val passEncoder: PassEncoder

    /**
     * Requested length of the final password.
     *
     * This must be `null` when [passEncoder] is a [SeedPassEncoder] and must
     * be non-null when [passEncoder] is a [StringPassEncoder].
     */
    val passwordLength: Int?

    /**
     * Maximum number of entropy bytes produced by the underlying
     * entropy-generation mechanism.
     *
     * This value is independent of [passEncoder] and [passwordLength].
     * The generated entropy bytes are subsequently encoded by [passEncoder]
     * and, for [StringPassEncoder], truncated to [passwordLength].
     */
    val maxEntropyByteSize: Int

    /**
     * Validates the password-length configuration for the selected encoder.
     *
     * @throws IllegalPasswordLengthException if the configuration is invalid.
     */
    fun validatePasswordLength() {
        validatePasswordLength(
            passEncoder = passEncoder,
            maxEntropyByteSize = maxEntropyByteSize,
            passwordLength = passwordLength
        )
    }


    companion object {

        /**
         * Validates the password-generation configuration for the given encoder.
         *
         * For [SeedPassEncoder], [passwordLength] must be `null` because the complete
         * encoded token is returned. For [StringPassEncoder], [passwordLength] must
         * be positive and must not exceed the encoded length that can be produced
         * from [maxEntropyByteSize].
         *
         * @param passEncoder Encoder used to produce the final token.
         * @param maxEntropyByteSize Maximum number of entropy bytes available for
         *   generating the token.
         * @param passwordLength Requested length of the final password, or `null`
         *   when the complete encoded token should be returned.
         * @throws IllegalPasswordLengthException if the configuration is invalid.
         */
        fun validatePasswordLength(
            passEncoder: PassEncoder,
            maxEntropyByteSize: Int,
            passwordLength: Int?
        ) {
            when (passEncoder) {
                is SeedPassEncoder -> {
                    if (passwordLength != null)
                        throw IllegalPasswordLengthException("Password length must be null for SeedPassEncoder.")
                }

                is StringPassEncoder -> {
                    if (passwordLength == null)
                        throw IllegalPasswordLengthException("Password length must not be null for StringPassEncoder.")

                    val tokenLength = passEncoder.getTokenLength(byteSize = maxEntropyByteSize)

                    if (passwordLength <= 0)
                        throw IllegalPasswordLengthException("Password length must be greater than 0.")

                    if (passwordLength > tokenLength)
                        throw IllegalPasswordLengthException("Requested Password length must not exceed $tokenLength, which is the length of the Token.")
                }
            }
        }
    }
}