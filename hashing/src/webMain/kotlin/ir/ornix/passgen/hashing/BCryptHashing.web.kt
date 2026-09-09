@file:OptIn(ExperimentalWasmJsInterop::class)

package ir.ornix.passgen.hashing

import ir.ornix.passgen.codec.BCryptBase64BinaryCodec
import kotlinx.coroutines.await
import kotlin.js.ExperimentalWasmJsInterop

private val bcryptBase64Codec = BCryptBase64BinaryCodec()

internal actual suspend fun digest(
    input: ByteArray,
    salt: ByteArray,
    cost: Int,
    addTerminator: Boolean
): ByteArray {
    val options = createBcryptOptions()
    options.password = input.toUint8Array()
    options.salt = salt.toUint8Array()
    options.costFactor = cost
    options.outputType = "encoded"

    val resultString = HashWasm.bcrypt(options).await()
    val hashPart = resultString.toString().takeLast(31)
    return bcryptBase64Codec.decode(hashPart)
}
