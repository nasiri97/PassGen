package ir.ornix.passgen.core.domain

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.HMAC
import dev.whyoleg.cryptography.algorithms.SHA512
import ir.ornix.passgen.core.common.hashing.Sha512Hasher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest

/**
 * A fake implementation of [HmacSigner] that stores keys in memory
 * and uses dev.whyoleg.cryptography for actual HMAC-SHA512 computations.
 */
class FakeHmacSigner : HmacSigner {
    private val masterKeyDigests = mutableMapOf<String, ByteArray>()
    private val sha512Hasher = Sha512Hasher()

    override fun registerKey(mkdId: String, rawMasterKey: ByteArray) = runTest {
        runBlocking {
            // As per HmacSigner.kt doc: rawKey is hashed with SHA-512 and stored
            masterKeyDigests[mkdId] = sha512Hasher.digest(rawMasterKey)
        }
    }

    override fun sign(mkdId: String, input: String): ByteArray {
        val hashedKey = masterKeyDigests[mkdId] ?: throw IllegalArgumentException("Key not found for keyId: $mkdId")
        return runBlocking {
            val hmacAlgorithm = CryptographyProvider.Default.get(HMAC)
            val keyDecoder = hmacAlgorithm.keyDecoder(SHA512)
            val importedKey = keyDecoder.decodeFromByteArray(HMAC.Key.Format.RAW, hashedKey)
            importedKey.signatureGenerator().generateSignature(input.encodeToByteArray())
        }
    }

    override fun hasMasterKeyDigest(mkdId: String): Boolean = masterKeyDigests.containsKey(mkdId)

    override fun deleteMasterKeyDigest(mkdId: String) {
        masterKeyDigests.remove(mkdId)
    }
}
