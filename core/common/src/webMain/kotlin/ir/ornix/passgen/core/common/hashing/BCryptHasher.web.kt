@file:OptIn(ExperimentalWasmJsInterop::class)

package ir.ornix.passgen.core.common.hashing

import ir.ornix.passgen.core.common.codec.BCryptBase64BinaryCodec
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
    options.password = byteArrayToUint8Array(input)
    options.salt = byteArrayToUint8Array(salt)
    options.costFactor = cost
    options.outputType = "encoded"

    val resultString = HashWasm.bcrypt(options).await()
    val hashPart = resultString.toString().takeLast(31)
    return bcryptBase64Codec.decode(hashPart)
}
