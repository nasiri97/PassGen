package ir.ornix.passgen.hashing

import ir.ornix.passgen.hashing.core.Hashing
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


object Argon2idHashing : Hashing {

    // The generated password is 72 bytes (144 Hex-Chars) (96 Base64-Chars) (90 Z85-Chars)
    override val outputByteSize = 72

    private const val ITERATIONS = 4
    private const val MEMORY_COST = 128 * 1024  // 131072 KB
    private const val PARALLELISM_FACTOR = 1

    private val mutex = Mutex()

    private val sha256Hashing = Sha256Hashing()

    override suspend fun digest(input: ByteArray): ByteArray {

        /**
         * We use a 16-byte salt
         *
         * The salt is deterministically derived from the input by hashing
         * the input with SHA-256 and taking the first 16 bytes.
         */
        val salt = sha256Hashing.digest(input).copyOfRange(0, 16)

        return digest(input, salt)
    }

    internal suspend fun digest(input: ByteArray, salt: ByteArray): ByteArray {
        return mutex.withLock {
            digest(
                input = input,
                salt = salt,
                iterations = ITERATIONS,
                memoryCost = MEMORY_COST,
                parallelismFactor = PARALLELISM_FACTOR,
                outputByteSize = outputByteSize
            )
        }
    }
}

expect internal suspend fun digest(
    input: ByteArray,
    salt: ByteArray,
    iterations: Int,
    memoryCost: Int,
    parallelismFactor: Int,
    outputByteSize: Int
): ByteArray