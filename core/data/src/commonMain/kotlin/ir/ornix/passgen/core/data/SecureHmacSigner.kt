package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.HmacSigner

expect class SecureHmacSigner : HmacSigner {

    override fun registerKey(keyId: String, rawKey: ByteArray)

    override fun sign(keyId: String, input: String): ByteArray

    override fun hasKey(keyId: String): Boolean

    override fun deleteKey(keyId: String)
}