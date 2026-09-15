package ir.ornix.passgen.core.data

import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64

actual object PlatformCrypto {
    private const val ALIAS = "PassGenConfigKey"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"

    init {
        try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
            if (!keyStore.containsAlias(ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(
                    "AES",
                    KEYSTORE_PROVIDER
                )
                val spec = android.security.keystore.KeyGenParameterSpec.Builder(
                    ALIAS,
                    android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or android.security.keystore.KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
                keyGenerator.init(spec)
                keyGenerator.generateKey()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual fun encrypt(data: String): String {
        return try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
            val secretKey = keyStore.getKey(ALIAS, null) as SecretKey
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(data.encodeToByteArray())
            
            val combined = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
            
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            data
        }
    }

    actual fun decrypt(data: String): String {
        return try {
            val combined = Base64.decode(data, Base64.NO_WRAP)
            if (combined.size < 12) return data
            
            val iv = ByteArray(12)
            System.arraycopy(combined, 0, iv, 0, iv.size)
            
            val encryptedBytes = ByteArray(combined.size - iv.size)
            System.arraycopy(combined, iv.size, encryptedBytes, 0, encryptedBytes.size)
            
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
            val secretKey = keyStore.getKey(ALIAS, null) as SecretKey
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            
            String(cipher.doFinal(encryptedBytes))
        } catch (e: Exception) {
            data
        }
    }
}
