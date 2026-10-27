package ir.ornix.passgen.core.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import ir.ornix.passgen.core.domain.Crypto
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Android Keystore-backed AES-256-GCM encryption for PassGen configuration data.
 *
 * Encrypted data format:
 * [ 12-byte IV ][ ciphertext ][ 16-byte GCM tag ]
 *
 * Uses "PassGen:config:v1" as Additional Authenticated Data (AAD), which
 * authenticates the data format without encrypting it.
 */
actual object PlatformCrypto : Crypto {
    private const val ALIAS = "PassGenConfigKey"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private val AAD = "PassGen:config:v1".toByteArray(Charsets.UTF_8)

    private const val IV_SIZE_BYTES = 12
    private const val TAG_SIZE_BITS = 128
    private const val TAG_SIZE_BYTES = TAG_SIZE_BITS / Byte.SIZE_BITS

    init {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        if (!keyStore.containsAlias(ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            )
            val spec = KeyGenParameterSpec.Builder(
                ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setKeySize(256)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }
    }

    actual override fun encrypt(data: ByteArray): ByteArray {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }

        val secretKey = keyStore.getKey(ALIAS, null) as SecretKey

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        cipher.updateAAD(AAD)

        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(data)

        return (iv + encryptedBytes)
    }

    actual override fun decrypt(data: ByteArray): ByteArray {
        // Minimum size: 12-byte IV + 16-byte GCM authentication tag.
        require(data.size >= IV_SIZE_BYTES + TAG_SIZE_BYTES) {
            "Encrypted data is too short"
        }

        val iv = data.copyOfRange(0, IV_SIZE_BYTES)
        val encryptedBytes = data.copyOfRange(IV_SIZE_BYTES, data.size)

        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }

        val secretKey = keyStore.getKey(ALIAS, null) as SecretKey

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(TAG_SIZE_BITS, iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        cipher.updateAAD(AAD)

        return cipher.doFinal(encryptedBytes)
    }
}
