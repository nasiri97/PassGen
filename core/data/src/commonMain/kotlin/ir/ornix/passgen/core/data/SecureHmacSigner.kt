package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.HmacSigner

expect class SecureHmacSigner : HmacSigner {

    override fun registerKey(mkdId: String, rawMasterKey: ByteArray)

    override fun sign(mkdId: String, input: String): ByteArray

    override fun hasMasterKeyDigest(mkdId: String): Boolean

    override fun deleteMasterKeyDigest(mkdId: String)
}