package ir.ornix.passgen.hashing

import ir.ornix.passgen.codec.BCryptBase64BinaryCodec
import ir.ornix.passgen.hashing.core.Hashing
import kotlinx.coroutines.await
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.set

actual class BCryptHashing : Hashing {

    actual override val outputByteSize = 23

    companion object {
        private const val COST = 12
        private val sha256Hashing = Sha256Hashing()
        private val bcryptBase64Codec = BCryptBase64BinaryCodec()
    }

    actual override suspend fun digest(input: ByteArray): ByteArray {
        val salt = sha256Hashing.digest(input).copyOfRange(0, 16)
        return digest(input, salt)
    }

    internal suspend fun digest(input: ByteArray, salt: ByteArray): ByteArray {
        val options = createBcryptOptions()
        options.password = input.toUint8Array()
        options.salt = salt.toUint8Array()
        options.cost = COST
        options.outputType = "string"

        val resultString = HashWasm.bcrypt(options).await()
        val hashPart = resultString.takeLast(31)
        return bcryptBase64Codec.decode(hashPart)
    }

    private fun ByteArray.toUint8Array(): Uint8Array {
        val result = Uint8Array(this.size)
        for (i in this.indices) {
            result[i] = this[i]
        }
        return result
    }
}
