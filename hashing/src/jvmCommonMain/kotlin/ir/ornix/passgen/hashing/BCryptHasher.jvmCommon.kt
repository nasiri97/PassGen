package ir.ornix.passgen.hashing

import org.bouncycastle.crypto.generators.BCrypt

internal actual suspend fun digest(
    input: ByteArray,
    salt: ByteArray,
    cost: Int,
    addTerminator: Boolean
): ByteArray {

    /**
     * BCrypt.generate() returns the raw EksBlowfish-derived output as
     * 24 bytes (192 bits). The standard BCrypt encoded format uses only
     * the first 23 bytes of this output, which are encoded into 31
     * BCrypt Base64 characters.
     */
    val output = BCrypt.generate(input, salt, cost, addTerminator)

    // Keep the 23-byte BCrypt hash portion.
    // 23 bytes encode to 31 BCrypt Base64 characters.
    return output.copyOfRange(0, 23)
}