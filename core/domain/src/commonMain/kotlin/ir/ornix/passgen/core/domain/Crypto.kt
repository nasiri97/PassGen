package ir.ornix.passgen.core.domain


/**
 * Provides encryption and decryption of binary data.
 *
 * Implementations are responsible for the cryptographic algorithm,
 * key management, and any required authentication or integrity protection.
 */
interface Crypto {
    fun encrypt(data: ByteArray): ByteArray
    fun decrypt(data: ByteArray): ByteArray
}