package ir.ornix.passgen.core.domain

interface MasterKeyRepository {
    fun saveMasterKey(key: String)
    fun hasMasterKey(): Boolean
    fun getMasterKey(): String?
}
