package ir.ornix.passgen.hashing

import ir.ornix.passgen.hashing.core.Hashing
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters

actual object Argon2Hashing : Hashing {

    // The generated password is 72 bytes (144 Hex-Chars) (96 Base64-Chars) (90 Z85-Chars)
    override val outputByteSize = 72

    private const val TYPE = Argon2Parameters.ARGON2_id
    private const val ITERATIONS = 4
    private const val MEMORY_COST = 128 * 1024  // 131072 KB
    private const val PARALLELISM_FACTOR = 1

    private val mutex = Mutex()

    private val sha256Hashing = Sha256Hashing()

    override suspend fun digest(input: ByteArray): ByteArray {
        // salt is 18 bytes (36 Hex-Chars) (24 Base64-Chars)
        val salt = sha256Hashing.digest(input).copyOfRange(0, 18)

        return digest(input, salt)
    }

    internal suspend fun digest(input: ByteArray, salt: ByteArray): ByteArray {
        return mutex.withLock {
            val params = Argon2Parameters.Builder(TYPE)
                .withSalt(salt)
                .withMemoryAsKB(MEMORY_COST)
                .withIterations(ITERATIONS)
                .withParallelism(PARALLELISM_FACTOR)
                .build()

            val generator = Argon2BytesGenerator()
            generator.init(params)

            val output = ByteArray(outputByteSize)
            generator.generateBytes(input, output)

            output
        }
    }
}