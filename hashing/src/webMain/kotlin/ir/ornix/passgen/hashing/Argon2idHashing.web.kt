@file:OptIn(ExperimentalWasmJsInterop::class)

package ir.ornix.passgen.hashing

import ir.ornix.passgen.codec.HexBinaryCodec
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
    options.password = input.toUint8Array()
    options.salt = salt.toUint8Array()
    options.iterations = iterations
    options.memorySize = memoryCost
    options.parallelism = parallelismFactor
    options.hashLength = outputByteSize
    options.outputType = "hex"

    val hexResult = HashWasm.argon2id(options).await()
    return hexCodec.decode(hexResult.toString())
}
