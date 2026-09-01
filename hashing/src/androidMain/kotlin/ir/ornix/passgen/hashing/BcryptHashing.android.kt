package ir.ornix.passgen.hashing


import org.mindrot.jbcrypt.BCrypt

actual class BcryptHashing : Hashing {

    override val outputByteSize = 24

    override suspend fun digest(input: ByteArray): ByteArray {
        val salt = BCrypt.gensalt(12)
        return BCrypt.hashpw(input.contentToString(), salt).toByteArray()
    }
}