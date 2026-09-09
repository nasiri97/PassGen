package ir.ornix.passgen.hashing

import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters


private const val TYPE = Argon2Parameters.ARGON2_id

internal actual suspend fun digest(
    input: ByteArray,
    salt: ByteArray,
    iterations: Int,
    memoryCost: Int,
    parallelismFactor: Int,
    outputByteSize: Int
): ByteArray {
    val params = Argon2Parameters.Builder(TYPE)
        .withSalt(salt)
        .withMemoryAsKB(memoryCost)
        .withIterations(iterations)
        .withParallelism(parallelismFactor)
        .build()

    val generator = Argon2BytesGenerator()
    generator.init(params)

    val output = ByteArray(outputByteSize)
    generator.generateBytes(input, output)

    return output
}