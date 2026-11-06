package ir.ornix.passgen.core.data

import android.os.Build
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import androidx.annotation.RequiresApi
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.SigningKeyNotFoundException
import ir.ornix.passgen.core.logging.Logger
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

actual object PlatformHmacSigner : HmacSigner {

    private fun aliasFor(mkdId: String) = "$ALIAS_PREFIX$mkdId"

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val HMAC_ALGORITHM = "HmacSHA512"
    private const val ALIAS_PREFIX = "hmac_sha512_"

    private val androidKeyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    }

    private fun getKeyOrNull(alias: String): SecretKey? {
        return try {
            val key = androidKeyStore.getKey(alias, null) as? SecretKey
            if (key == null) Logger.e("Signing key not founded for $alias")
            key
        } catch (e: Exception) {
            Logger.e("Failed to find signing key for $alias", e)
            null
        }
    }

    private fun registerKeyInStrongBox(
        mkdId: String,
        secretKeySpec: SecretKeySpec
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            Logger.i("StrongBox requires SDK version 31 or higher. Current SDK version: ${Build.VERSION.SDK_INT}.")
            return false
        }
        val alias = aliasFor(mkdId)

        try {
            androidKeyStore.setEntry(
                alias,
                KeyStore.SecretKeyEntry(secretKeySpec),
                protectionStrongBox()
            )
        } catch (e: Exception) {
            Logger.e("Failed to register the key in StrongBox", e)
            return false
        }

        val isHmacSha512Supported = isHmacSha512Supported(mkdId)

        if (!isHmacSha512Supported) {
            androidKeyStore.deleteEntry(alias)
            Logger.i("The StrongBox key does not support HMAC-SHA512 and has been deleted.")
        }

        return isHmacSha512Supported
    }

    private fun registerKeyInTEE(
        mkdId: String,
        secretKeySpec: SecretKeySpec
    ): Boolean {
        val alias = aliasFor(mkdId)

        try {
            androidKeyStore.setEntry(
                alias,
                KeyStore.SecretKeyEntry(secretKeySpec),
                protectionDefault()
            )
        } catch (e: Exception) {
            Logger.e("Failed to register the key in TEE", e)
            return false
        }

        // Verify that the imported key is hardware-backed and supports HMAC-SHA512
        val isHmacSha512Supported = isHmacSha512Supported(mkdId)

        if (!isHmacSha512Supported) {
            androidKeyStore.deleteEntry(alias)
            Logger.i("The TEE key does not support HMAC-SHA512 and has been deleted.")
        }

        return isHmacSha512Supported
    }

    actual override suspend fun registerKey(mkdId: String, rawMasterKey: ByteArray): Boolean {
        val hashedKey = try {
            MessageDigest.getInstance("SHA-512").digest(rawMasterKey)
        } catch (e: Exception) {
            Logger.e("Failed to register the key: SHA-512 hash calculation failed.", e)
            return false
        } finally {
            rawMasterKey.fill(0)
        }

        return try {
            val secretKeySpec = SecretKeySpec(
                hashedKey,
                KeyProperties.KEY_ALGORITHM_HMAC_SHA512
            )

            // Try StrongBox first, fall back to TEE
            registerKeyInStrongBox(mkdId, secretKeySpec) ||
                    registerKeyInTEE(mkdId, secretKeySpec)
        } catch (e: Exception) {
            Logger.e("Filed to register the key", e)
            false
        } finally {
            hashedKey.fill(0)
        }
    }

    actual override suspend fun hasMasterKeyDigest(mkdId: String): Boolean =
        androidKeyStore.containsAlias(aliasFor(mkdId))

    actual override suspend fun deleteMasterKeyDigest(mkdId: String) {
        androidKeyStore.deleteEntry(aliasFor(mkdId))
    }

    actual override suspend fun sign(mkdId: String, input: String): ByteArray {
        val alias = aliasFor(mkdId)
        val key = getKeyOrNull(alias) ?: throw SigningKeyNotFoundException(mkdId)

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

    private fun isHmacSha512Supported(mkdId: String): Boolean {
        val alias = aliasFor(mkdId)
        val secretKey = getKeyOrNull(alias) ?: return false

        return try {
            val mac = Mac.getInstance(HMAC_ALGORITHM)
            mac.init(secretKey)
            mac.doFinal(byteArrayOf(0x01))
            true
        } catch (e: Exception) {
            Logger.e("HMAC-SHA512 is not supported by this key.", e)
            false
        }
    }
}
