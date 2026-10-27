package ir.ornix.passgen.core.common.hashing

internal actual suspend fun digest(
    input: ByteArray,
    salt: ByteArray,
    cost: Int,
    addTerminator: Boolean
): ByteArray {
    throw UnsupportedOperationException("BCrypt is not supported on iOS")
}
