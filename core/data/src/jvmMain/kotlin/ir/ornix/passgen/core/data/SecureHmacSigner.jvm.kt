package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.SigningKeyNotFoundException
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

actual class SecureHmacSigner : HmacSigner {

    private companion object {
        const val HMAC_ALGORITHM = "HmacSHA512"
        const val DIGEST_ALGORITHM = "SHA-512"
    }

    private val keys = ConcurrentHashMap<String, SecretKey>()

    actual override suspend fun registerKey(mkdId: String, rawMasterKey: ByteArray) {
        val hashedKey = MessageDigest.getInstance(DIGEST_ALGORITHM).digest(rawMasterKey)
        try {
            val secretKey = SecretKeySpec(hashedKey, HMAC_ALGORITHM)
            keys[mkdId] = secretKey
        } finally {
            rawMasterKey.fill(0)
            hashedKey.fill(0)
        }
    }

    actual override suspend fun sign(mkdId: String, input: String): ByteArray {
        val secretKey = keys[mkdId] ?: throw SigningKeyNotFoundException(mkdId)
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(secretKey)
        return mac.doFinal(input.toByteArray(Charsets.UTF_8))
    }

    actual override suspend fun hasMasterKeyDigest(mkdId: String): Boolean =
        keys.containsKey(mkdId)

    actual override suspend fun deleteMasterKeyDigest(mkdId: String) {
        keys.remove(mkdId)
    }
}
