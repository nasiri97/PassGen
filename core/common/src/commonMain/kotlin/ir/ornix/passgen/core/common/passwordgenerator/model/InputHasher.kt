package ir.ornix.passgen.core.common.passwordgenerator.model

import ir.ornix.passgen.core.common.hashing.Argon2IdHasher
import ir.ornix.passgen.core.common.hashing.BCryptHasher
import ir.ornix.passgen.core.common.hashing.Sha256Hasher
import ir.ornix.passgen.core.common.hashing.Sha512Hasher
import ir.ornix.passgen.core.common.hashing.core.Hasher
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Base class for all hashing algorithm configurations.
 * Each subclass defines a unique [key] and can create its corresponding [Hasher][ir.ornix.passgen.core.common.hashing.core.Hasher] instance.
 */
@Serializable(with = InputHasherSerializer::class)
sealed class InputHasher(override val key: String) : KeyBasedType<Hasher>() {

    suspend fun digest(input: ByteArray): ByteArray {
        return instance.digest(input)
    }

    val outputByteSize: Int
        get() = instance.outputByteSize

    object SHA256 : InputHasher(KEY_INPUT_HASHER_SHA256) {
        override val instance = Sha256Hasher()
    }

    object SHA512 : InputHasher(KEY_INPUT_HASHER_SHA512) {
        override val instance = Sha512Hasher()
    }

    object BCrypt : InputHasher(KEY_INPUT_HASHER_BCRYPT_COST_12_SALT_FIRST_16B_OF_SHA256_OF_INPUT) {
        override val instance = BCryptHasher()
    }

    object ARGON2ID :
        InputHasher(
            KEY_INPUT_HASHER_ARGON2ID_ITERATIONS_4_MEMORY_128MB_PARALLELISM_1_OUTPUT_64B_SALT_FIRST_16B_OF_SHA256_OF_INPUT
        ) {
        override val instance = Argon2IdHasher
    }


    val shortName
        get() = when (key) {
            KEY_INPUT_HASHER_SHA256 -> "SHA256"
            KEY_INPUT_HASHER_SHA512 -> "SHA512"
            KEY_INPUT_HASHER_BCRYPT_COST_12_SALT_FIRST_16B_OF_SHA256_OF_INPUT -> "BCRYPT"
            KEY_INPUT_HASHER_ARGON2ID_ITERATIONS_4_MEMORY_128MB_PARALLELISM_1_OUTPUT_64B_SALT_FIRST_16B_OF_SHA256_OF_INPUT -> "ARGON2ID"
            else -> throw IllegalArgumentException("Unknown InputHasher key: $key")
        }

    val fullName
        get() = when (key) {
            KEY_INPUT_HASHER_SHA256 -> "SHA-256"
            KEY_INPUT_HASHER_SHA512 -> "SHA-512"
            KEY_INPUT_HASHER_BCRYPT_COST_12_SALT_FIRST_16B_OF_SHA256_OF_INPUT -> "Bcrypt"
            KEY_INPUT_HASHER_ARGON2ID_ITERATIONS_4_MEMORY_128MB_PARALLELISM_1_OUTPUT_64B_SALT_FIRST_16B_OF_SHA256_OF_INPUT -> "Argon2id"
            else -> throw IllegalArgumentException("Unknown InputHasher key: $key")
        }

    val description
        get() = when (key) {
            KEY_INPUT_HASHER_SHA256 -> null
            KEY_INPUT_HASHER_SHA512 -> null
            KEY_INPUT_HASHER_BCRYPT_COST_12_SALT_FIRST_16B_OF_SHA256_OF_INPUT ->
                "(Cost 12, Salt: First 16 bytes of the SHA-256 output of the input)"

            KEY_INPUT_HASHER_ARGON2ID_ITERATIONS_4_MEMORY_128MB_PARALLELISM_1_OUTPUT_64B_SALT_FIRST_16B_OF_SHA256_OF_INPUT ->
                "(4 iterations, 128 MB memory, parallelism 1, 64-byte output, Salt: First 16 bytes of the SHA-256 output of the input)"

            else -> throw IllegalArgumentException("Unknown InputHasher key: $key")
        }

    companion object {

        const val KEY_INPUT_HASHER_SHA256 = "INPUT_HASHER_SHA256"
        const val KEY_INPUT_HASHER_SHA512 = "INPUT_HASHER_SHA512"
        const val KEY_INPUT_HASHER_BCRYPT_COST_12_SALT_FIRST_16B_OF_SHA256_OF_INPUT =
            "INPUT_HASHER_BCRYPT_COST_12_SALT_FIRST_16B_OF_SHA256_OF_INPUT"
        const val KEY_INPUT_HASHER_ARGON2ID_ITERATIONS_4_MEMORY_128MB_PARALLELISM_1_OUTPUT_64B_SALT_FIRST_16B_OF_SHA256_OF_INPUT =
            "INPUT_HASHER_ARGON2ID_ITERATIONS_4_MEMORY_128MB_PARALLELISM_1_OUTPUT_64B_SALT_FIRST_16B_OF_SHA256_OF_INPUT"

        val allItems by lazy {
            listOf(
                SHA256,
                SHA512,
                BCrypt,
                ARGON2ID
            )
        }

        fun fromKey(key: String): InputHasher = when (key) {
            KEY_INPUT_HASHER_SHA256 -> SHA256
            KEY_INPUT_HASHER_SHA512 -> SHA512
            KEY_INPUT_HASHER_BCRYPT_COST_12_SALT_FIRST_16B_OF_SHA256_OF_INPUT -> BCrypt
            KEY_INPUT_HASHER_ARGON2ID_ITERATIONS_4_MEMORY_128MB_PARALLELISM_1_OUTPUT_64B_SALT_FIRST_16B_OF_SHA256_OF_INPUT -> ARGON2ID
            else -> throw IllegalArgumentException("Unknown InputHasher key: $key")
        }
    }
}

object InputHasherSerializer : KSerializer<InputHasher> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("InputHasher", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: InputHasher) = encoder.encodeString(value.key)
    override fun deserialize(decoder: Decoder): InputHasher =
        InputHasher.fromKey(decoder.decodeString())
}
