package ir.ornix.passgen.hashing

import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

actual object Argon2Hashing : Hashing {

    const val TYPE = Argon2Parameters.ARGON2_id
    const val ITERATIONS = 4
    const val MEMORY_COST = 128 * 1024  // KB
    const val PARALLELISM_FACTOR = 1

    private val mutex = Mutex()
    override val outputByteSize = 64
    val salt = "12345678asdfghjk".toByteArray()

    override suspend fun digest(input: ByteArray): ByteArray {
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