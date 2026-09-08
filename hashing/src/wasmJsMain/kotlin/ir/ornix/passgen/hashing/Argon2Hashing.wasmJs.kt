package ir.ornix.passgen.hashing

import ir.ornix.passgen.codec.HexBinaryCodec
import ir.ornix.passgen.hashing.core.Hashing
import kotlinx.coroutines.await
import kotlin.js.JsAny

actual object Argon2Hashing : Hashing {

    actual override val outputByteSize = 72

    private const val ITERATIONS = 4
    private const val MEMORY_COST = 128 * 1024  // 128 MB
    private const val PARALLELISM_FACTOR = 1

    private val hexCodec = HexBinaryCodec(false)
    private val sha256Hashing = Sha256Hashing()

    actual override suspend fun digest(input: ByteArray): ByteArray {
        val salt = sha256Hashing.digest(input).copyOfRange(0, 18)
        return digest(input, salt)
    }

    internal suspend fun digest(input: ByteArray, salt: ByteArray): ByteArray {
        val options = createArgon2Options()
        options.password = input.toUint8Array()
        options.salt = salt.toUint8Array()
        options.iterations = ITERATIONS
        options.memorySize = MEMORY_COST
        options.parallelism = PARALLELISM_FACTOR
        options.hashLength = outputByteSize
        options.outputType = "hex"

        val hexResult = HashWasm.argon2id(options).await()
        return hexCodec.decode(hexResult.toString())
    }
}

@JsFun("(size) => new Uint8Array(size)")
private external fun createUint8Array(size: Int): JsAny

@JsFun("(array, index, value) => array[index] = value")
private external fun setUint8ArrayValue(array: JsAny, index: Int, value: Int)

internal fun ByteArray.toUint8Array(): JsAny {
    val result = createUint8Array(this.size)
    for (i in this.indices) {
        setUint8ArrayValue(result, i, this[i].toInt() and 0xFF)
    }
    return result
}
