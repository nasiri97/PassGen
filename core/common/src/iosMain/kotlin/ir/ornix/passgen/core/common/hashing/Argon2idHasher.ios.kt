package ir.ornix.passgen.core.common.hashing

internal actual suspend fun digest(
    input: ByteArray,
    salt: ByteArray,
    iterations: Int,
    memoryCost: Int,
    parallelismFactor: Int,
    outputByteSize: Int
): ByteArray {
    throw UnsupportedOperationException("Argon2id is not supported on iOS")
}
