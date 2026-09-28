package ir.ornix.passgen.core.common.passwordgenerator.kdf

import ir.ornix.passgen.core.common.codec.core.Decoder
import ir.ornix.passgen.core.common.passwordgenerator.core.PassGen
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder

/**
 * Generates passwords by deriving entropy from an input using a key
 * derivation function.
 *
 * The maximum entropy size is determined by [inputHasher]'s output size.
 * The derived bytes are encoded using [passEncoder].
 *
 * For [SeedPassEncoder], the complete encoded token is returned. For
 * [StringPassEncoder], the encoded token is truncated to [passwordLength].
 */
data class KDFPassGen(
    val inputHasher: InputHasher,
    override val passEncoder: PassEncoder,
    override val passwordLength: Int?
) : PassGen {

    /**
     * Maximum number of entropy bytes produced by [inputHasher].
     *
     * This value is determined by the configured hashing/KDF algorithm and
     * is independent of [passEncoder] and [passwordLength].
     */
    override val maxEntropyByteSize = inputHasher.outputByteSize

    init {
        validatePasswordLength()
    }

    private val tokenGen = TokenGen(
        inputHasher = inputHasher,
        outputPassEncoder = passEncoder
    )

    /**
     * Generates a password by decoding the string input and deriving
     * entropy from the resulting bytes.
     *
     * @param input Input string to derive the password from.
     * @param inputDecoder Decoder used to convert [input] into bytes.
     * @return The generated password, or `null` if token generation fails.
     */
    suspend fun generate(input: String, inputDecoder: Decoder): String? {
        return generate(inputDecoder.decode(input))
    }

    /**
     * Generates a password by deriving entropy from the given input bytes.
     *
     * @param input Input bytes used by the key derivation function.
     * @return The generated password, or `null` if token generation fails.
     */
    suspend fun generate(input: ByteArray): String? {
        val token = tokenGen.getToken(input)

        return when (passEncoder) {
            is SeedPassEncoder -> token
            is StringPassEncoder -> token?.substring(0, passwordLength!!)
        }
    }
}