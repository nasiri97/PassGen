package ir.ornix.passgen.core.common.hashing

import ir.ornix.passgen.core.common.hashing.util.BCryptCore


internal actual suspend fun digest(
    input: ByteArray,
    salt: ByteArray,
    cost: Int,
    addTerminator: Boolean
): ByteArray {
    val raw = BCryptCore.generate(input, salt, cost, addTerminator)
    return raw.copyOfRange(0, 23)
}