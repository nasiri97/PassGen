package ir.ornix.passgen.core.domain

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.HMAC
import dev.whyoleg.cryptography.algorithms.SHA512
import ir.ornix.passgen.core.common.hashing.Sha512Hasher

/**
 * A fake implementation of [HmacSigner] that stores keys in memory
 * and uses dev.whyoleg.cryptography for actual HMAC-SHA512 computations.
 */
class FakeHmacSigner : HmacSigner {
    private val masterKeyDigests = mutableMapOf<String, ByteArray>()
    private val sha512Hasher = Sha512Hasher()

    override suspend fun registerKey(mkdId: String, rawMasterKey: ByteArray) {
        // As per HmacSigner.kt doc: rawKey is hashed with SHA-512 and stored
        masterKeyDigests[mkdId] = sha512Hasher.digest(rawMasterKey)
    }

    override suspend fun sign(mkdId: String, input: String): ByteArray {
        val hashedKey = masterKeyDigests[mkdId]
            ?: throw IllegalArgumentException("Key not found for keyId: $mkdId")

        val hmacAlgorithm = CryptographyProvider.Default.get(HMAC)
        val keyDecoder = hmacAlgorithm.keyDecoder(SHA512)
        val importedKey = keyDecoder.decodeFromByteArray(HMAC.Key.Format.RAW, hashedKey)
        return importedKey.signatureGenerator().generateSignature(input.encodeToByteArray())

    }

    override suspend fun hasMasterKeyDigest(mkdId: String): Boolean =
        masterKeyDigests.containsKey(mkdId)

    override suspend fun deleteMasterKeyDigest(mkdId: String) {
        masterKeyDigests.remove(mkdId)
    }
}
