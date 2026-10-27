package ir.ornix.passgen.core.common.passwordgenerator.random

import ir.ornix.passgen.core.common.passwordgenerator.core.PassGen
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder

/**
 * Generates passwords from cryptographically secure random entropy.
 *
 * The generator produces [maxEntropyByteSize] random bytes using a
 * cryptographically secure random number generator. These bytes are then
 * encoded using [passEncoder].
 *
 * For [SeedPassEncoder], the complete encoded token is returned. For
 * [StringPassEncoder], the encoded token is truncated to [passwordLength].
 */
data class RandomPassGen(
    override val passEncoder: PassEncoder,
    override val passwordLength: Int?
) : PassGen {

    companion object {
        const val RANDOM_PASS_GEN_MAX_ENTROPY_BYTE_SIZE = 64
    }

    /**
     * Maximum number of random entropy bytes generated for each password.
     */
    override val maxEntropyByteSize = RANDOM_PASS_GEN_MAX_ENTROPY_BYTE_SIZE

    init {
        validatePasswordLength()
    }

    /**
     * Generates a password from cryptographically secure random entropy.
     *
     * @return The generated password.
     */
    suspend fun generate(): String {
        val token = passEncoder.encode(secureRandomBytes(maxEntropyByteSize))

        return when (passEncoder) {
            is SeedPassEncoder -> token
            is StringPassEncoder -> token.substring(0, passwordLength!!)
        }
    }
}