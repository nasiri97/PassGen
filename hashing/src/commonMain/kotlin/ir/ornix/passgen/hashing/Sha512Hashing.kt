package ir.ornix.passgen.hashing

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.SHA512

class Sha512Hashing : Hashing {

    override val outputByteSize = 64

    companion object {
        const val SHA_512 = "SHA-512"

        private val sha512 = CryptographyProvider.Default
            .get(SHA512)
            .hasher()
    }

    /**
     * @return 32 bytes (64 Hex chars) (44 Base64 chars)
     * Hexadecimal representation: 128 hex characters
     * Base64 representation (standard, padded): 88 characters (ends with ==)
     */
    override suspend fun digest(input: ByteArray): ByteArray {
        return sha512.hash(input)
    }
}