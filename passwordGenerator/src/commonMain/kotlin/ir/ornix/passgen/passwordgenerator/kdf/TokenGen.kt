package ir.ornix.passgen.passwordgenerator.kdf

import ir.ornix.passgen.logcore.Logger
import ir.ornix.passgen.passwordgenerator.model.InputHasher
import ir.ornix.passgen.passwordgenerator.model.PassEncoder

/**
 * Generates a deterministic token from a byte array input using a hashing algorithm
 * and exposes it in a fixed-length encoded representation.
 *
 * The token is cached per input and regenerated only when the input changes.
 *
 * @param inputHasher The hashing algorithm used to generate the raw token bytes.
 * @param outputPassEncoder Encoder used to convert the generated token into the desired textual representation (e.g., Base64, Hex).
 */
internal class TokenGen(
    private val inputHasher: InputHasher,
    private val outputPassEncoder: PassEncoder
) {

    private var input: ByteArray? = null
    private var token: ByteArray? = null


    /**
     * Generates and caches the raw token bytes for the given input.
     *
     * This method performs hashing and should be called only when the input
     * changes or no cached token is available.
     *
     * @param input The input byte array from which the token is generated.
     */
    private suspend fun generate(input: ByteArray) {
        try {
            this.input = input
            token = inputHasher.digest(input)
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
     * @param input The input byte array used to generate the token.
     * @return The encoded token string, or null if token generation fails.
     */
    suspend fun getToken(input: ByteArray): String? {
        if (!this.input.contentEquals(input) || token == null) generate(input)

        return token?.let {
            outputPassEncoder.encode(it)
        }
    }
}