package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.HmacSigner

expect class SecureHmacSigner() : HmacSigner {

    override suspend fun registerKey(mkdId: String, rawMasterKey: ByteArray)

    override suspend fun sign(mkdId: String, input: String): ByteArray

    override suspend fun hasMasterKeyDigest(mkdId: String): Boolean

    override suspend fun deleteMasterKeyDigest(mkdId: String)
}