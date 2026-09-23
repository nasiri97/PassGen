package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.SigningKeyNotFoundException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SecureHmacSignerTest {

    @Test
    fun testRegisterSignHasAndDeleteKey() = runTest {
        val signer = SecureHmacSigner()
        val keyId = "test_key_1"
        val rawMasterKey = "my_secret_master_key_12345".encodeToByteArray()

        assertFalse(signer.hasMasterKeyDigest(keyId))

        signer.registerKey(keyId, rawMasterKey)
        assertTrue(signer.hasMasterKeyDigest(keyId))

        val signature1 = signer.sign(keyId, "domain.com")
        assertEquals(64, signature1.size)

        val signature2 = signer.sign(keyId, "domain.com")
        assertEquals(signature1.toHex(), signature2.toHex())

        val signature3 = signer.sign(keyId, "otherdomain.com")
        assertEquals(64, signature3.size)
        assertNotEquals(signature1.toHex(), signature3.toHex())

        signer.deleteMasterKeyDigest(keyId)
        assertFalse(signer.hasMasterKeyDigest(keyId))

        assertFailsWith<SigningKeyNotFoundException> {
            signer.sign(keyId, "domain.com")
        }
    }

    private fun ByteArray.toHex(): String = joinToString("") {
        (it.toInt() and 0xFF).toString(16).padStart(2, '0')
    }
}
