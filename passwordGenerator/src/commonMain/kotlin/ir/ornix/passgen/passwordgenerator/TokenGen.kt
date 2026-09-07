package ir.ornix.passgen.passwordgenerator

import ir.ornix.passgen.codec.core.Decoder
import ir.ornix.passgen.hashing.core.Hashing
import ir.ornix.passgen.logcore.Logger

/**
 * Generates a deterministic token from a textual input using a hashing algorithm
 * and exposes it in a fixed-length encoded representation.
 *
 * The token is cached per input and regenerated only when the input changes.
 *
 * @param hashing The hashing algorithm used to generate the raw token bytes.
 * @param inputDecoder Decoder used to convert the input string into a byte array before hashing (e.g., UTF-8).
 * @param outputEncoder Fixed-length encoder used to convert the generated token
 * into the desired textual representation (e.g., Base64, Hex).
 */
class TokenGen(
    private val hashing: Hashing,
    private val inputDecoder: Decoder,
    private val outputEncoder: FixedLengthEncoder
) {

    companion object {

        /**
         * The number of encoded units required to represent
         * the output of the given [hashing] algorithm after applying the specified
         * [outputEncoder].
         *
         * The hash output size is fixed and defined by the hashing algorithm itself.
         * This method delegates the size calculation to the encoder, which may
         * introduce expansion or padding depending on its encoding granularity.
         *
         * @param hashing Hashing algorithm that produces a fixed-length byte output.
         * @param outputEncoder Encoder used to transform the hash output into its
         * encoded representation.
         * @return Number of encoded units needed for the encoded hash output.
         */
        fun calculateTokenLength(
            hashing: Hashing,
            outputEncoder: FixedLengthEncoder
        ): Int {

            /** Size of the raw hash output produced by the hashing algorithm, in bytes.*/
            val tokenByteSize = hashing.outputByteSize

            return outputEncoder.encodedUnitCount(tokenByteSize)
        }
    }

    private var input: String? = null
    private var token: ByteArray? = null


    /**
     * The number of encoded units required to represent
     * the output of the given [hashing] algorithm after applying the specified
     * [outputEncoder].
     */
    val tokenLength = calculateTokenLength(
        hashing = hashing,
        outputEncoder = outputEncoder
    )


    /**
     * Generates and caches the raw token bytes for the given input.
     *
     * This method performs hashing and should be called only when the input
     * changes or no cached token is available.
     *
     * @param input The input string from which the token is generated.
     */
    private suspend fun generate(input: String) {
        try {
            this.input = input
            token = hashing.digest(
                input = input,
                inputDecoder = inputDecoder
            )
        } catch (e: Exception) {
            Logger.e("Failed to generate token!", e)
            token = null
        }
    }


    /**
     * Returns the encoded token for the given input.
     *
     * If the input has not changed since the last invocation, the cached token
     * is reused. Otherwise, the token is regenerated.
     *
     * The returned value is truncated to the maximum deterministic length
     * supported by the configured fixed-length encoder.
     *
     * @param input The input string used to generate the token.
     * @return The encoded token string, or null if token generation fails.
     */
    suspend fun getToken(input: String): String? {
        if (this.input != input || token == null) generate(input)

        return token?.let {
            outputEncoder.encode(it).substring(0, tokenLength)
        }
    }
}