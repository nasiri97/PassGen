package ir.ornix.passgen.core.domain

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.HMAC
import dev.whyoleg.cryptography.algorithms.SHA512
import ir.ornix.passgen.hashing.Sha512Hasher
import kotlinx.coroutines.runBlocking

class FakeHmacSigner : HmacSigner {
    private val keys = mutableMapOf<String, ByteArray>()
    private val sha512Hasher = Sha512Hasher()

    override fun registerKey(keyId: String, rawKey: ByteArray) {
        runBlocking {
            keys[keyId] = sha512Hasher.digest(rawKey)
        }
    }

    override fun sign(keyId: String, input: String): ByteArray {
        val hashedKey = keys[keyId] ?: throw IllegalArgumentException("Key not found for keyId: $keyId")
        return runBlocking {
            val hmacAlgorithm = CryptographyProvider.Default.get(HMAC)
            val keyDecoder = hmacAlgorithm.keyDecoder(SHA512)
            val importedKey = keyDecoder.decodeFromByteArray(HMAC.Key.Format.RAW, hashedKey)
            importedKey.signatureGenerator().generateSignature(input.encodeToByteArray())
        }
    }

    override fun hasKey(keyId: String): Boolean = keys.containsKey(keyId)

    override fun deleteKey(keyId: String) {
        keys.remove(keyId)
    }
}
