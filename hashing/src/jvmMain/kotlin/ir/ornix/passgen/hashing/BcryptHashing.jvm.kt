package ir.ornix.passgen.hashing

actual class BcryptHashing : Hashing {
    actual override val outputByteSize: Int
        get() = TODO("Not yet implemented")

    actual override suspend fun digest(input: ByteArray): ByteArray {
        TODO("Not yet implemented")
    }
}
