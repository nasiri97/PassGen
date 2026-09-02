package ir.ornix.passgen.hashing

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.SHA256

class Sha256Hashing : Hashing {

    override val outputByteSize = 32

    companion object {
        const val SHA_256 = "SHA-256"

        private val sha256 = CryptographyProvider.Default
            .get(SHA256)
            .hasher()
    }

    /**
     * @return 32 bytes
     * Hexadecimal representation: 64 hex characters
     * Base64 representation (standard, padded): 44 characters (ends with ==)
     */
    override suspend fun digest(input: ByteArray): ByteArray {
        return sha256.hash(input)
    }
}