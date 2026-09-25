package ir.ornix.passgen.core.common.hashing

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.SHA256
import ir.ornix.passgen.core.common.hashing.core.Hasher

class Sha256Hasher : Hasher {

    override val outputByteSize = 32

    companion object {
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
        return digestBlocking(input)
    }

    fun digestBlocking(input: ByteArray): ByteArray {
        return sha256.hashBlocking(input)
    }
}