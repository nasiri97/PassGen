@file:OptIn(ExperimentalWasmJsInterop::class)

package ir.ornix.passgen.core.common.hashing

import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import kotlinx.coroutines.await
import kotlin.js.ExperimentalWasmJsInterop

private val hexCodec = HexBinaryCodec(false)

internal actual suspend fun digest(
    input: ByteArray,
    salt: ByteArray,
    iterations: Int,
    memoryCost: Int,
    parallelismFactor: Int,
    outputByteSize: Int
): ByteArray {
    val options = createArgon2Options()
    if (input.size == 0) {
        setupEmptyPasswordOption(options)
    } else {
        options.password = byteArrayToUint8Array(input)
    }
    options.salt = byteArrayToUint8Array(salt)
    options.iterations = iterations
    options.memorySize = memoryCost
    options.parallelism = parallelismFactor
    options.hashLength = outputByteSize
    options.outputType = "hex"

    val hexResult = HashWasm.argon2id(options).await()
    return hexCodec.decode(hexResult.toString())
}
