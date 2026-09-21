package ir.ornix.passgen.core.data

import android.os.Build
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import androidx.annotation.RequiresApi
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.SigningKeyNotFoundException
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


actual class SecureHmacSigner : HmacSigner {

    private fun aliasFor(mkdId: String) = "$ALIAS_PREFIX$mkdId"

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val HMAC_ALGORITHM = "HmacSHA512"
        const val ALIAS_PREFIX = "hmac_sha512_"
    }

    private val androidKeyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    }

    actual override fun registerKey(mkdId: String, rawMasterKey: ByteArray) {
        val alias = aliasFor(mkdId)

        // Hash the raw key to a fixed 64-byte (512-bit) value — this both
        // normalizes the size to something Keymaster reliably accepts, and
        // means the original rawKey bytes are never the value actually stored.
        val hashedKey = MessageDigest.getInstance("SHA-512").digest(rawMasterKey)

        val secretKeySpec = SecretKeySpec(hashedKey, KeyProperties.KEY_ALGORITHM_HMAC_SHA512)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // API 31+: StrongBox flag supported on KeyProtection
                try {
                    androidKeyStore.setEntry(
                        alias,
                        KeyStore.SecretKeyEntry(secretKeySpec),
                        protectionStrongBox()
                    )
                } catch (e: Exception) {
                    // Broadened: StrongBox HMAC import commonly fails with a plain
                    // KeyStoreException ("Failed to import secret key"), not just
                    // StrongBoxUnavailableException — many StrongBox chips only
                    // support key generation, not import, for HMAC keys.
                    androidKeyStore.setEntry(
                        alias,
                        KeyStore.SecretKeyEntry(secretKeySpec),
                        protectionDefault()
                    )
                }
            } else {
                // API 26–30: no StrongBox import flag available — use standard TEE-backed protection
                androidKeyStore.setEntry(
                    alias,
                    KeyStore.SecretKeyEntry(secretKeySpec),
                    protectionDefault()
                )
            }
        } finally {
            rawMasterKey.fill(0)
            hashedKey.fill(0)
        }
    }


    actual override fun hasMasterKeyDigest(mkdId: String): Boolean =
        androidKeyStore.containsAlias(aliasFor(mkdId))

    actual override fun deleteMasterKeyDigest(mkdId: String) {
        androidKeyStore.deleteEntry(aliasFor(mkdId))
    }


    actual override fun sign(mkdId: String, input: String): ByteArray {
        val alias = aliasFor(mkdId)
        val key = androidKeyStore.getKey(alias, null) as? SecretKey
            ?: throw SigningKeyNotFoundException(mkdId)

        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(key) // key stays in hardware — never enters app memory
        return mac.doFinal(input.toByteArray(Charsets.UTF_8))
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun protectionStrongBox(): KeyProtection =
        KeyProtection.Builder(KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
            .setDigests(KeyProperties.DIGEST_SHA512)
            .setIsStrongBoxBacked(true)
            .build()

    private fun protectionDefault(): KeyProtection =
        KeyProtection.Builder(KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
            .setDigests(KeyProperties.DIGEST_SHA512)
            .build()
}