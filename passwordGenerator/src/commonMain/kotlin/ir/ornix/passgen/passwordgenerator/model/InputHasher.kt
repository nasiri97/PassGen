package ir.ornix.passgen.passwordgenerator.model

import ir.ornix.passgen.hashing.Argon2IdHasher
import ir.ornix.passgen.hashing.BCryptHasher
import ir.ornix.passgen.hashing.Sha256Hasher
import ir.ornix.passgen.hashing.Sha512Hasher
import ir.ornix.passgen.hashing.core.Hasher
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Base class for all hashing algorithm configurations.
 * Each subclass defines a unique [key] and can create its corresponding [Hasher][ir.ornix.passgen.hashing.core.Hasher] instance.
 */
@Serializable(with = InputHasherSerializer::class)
sealed class InputHasher(override val key: String) : KeyBasedType<Hasher>() {

    suspend fun digest(
        input: String,
        inputDecoder: ir.ornix.passgen.codec.core.Decoder
    ): ByteArray {
        return instance.digest(input = input, inputDecoder = inputDecoder)
    }

    val outputByteSize: Int
        get() = instance.outputByteSize

    object SHA256 : InputHasher(KEY_SHA256) {
        override val instance = Sha256Hasher()
    }

    object SHA512 : InputHasher(KEY_SHA512) {
        override val instance = Sha512Hasher()
    }

    object BCrypt : InputHasher(KEY_BCRYPT) {
        override val instance = BCryptHasher()
    }

    object ARGON2ID : InputHasher(KEY_ARGON2_ID) {
        override val instance = Argon2IdHasher
    }

    companion object {

        const val KEY_SHA256 = "SHA256"
        const val KEY_SHA512 = "SHA512"
        const val KEY_BCRYPT = "BCRYPT"
        const val KEY_ARGON2_ID = "ARGON2_ID"

        val items by lazy {
            listOf(
                SHA256,
                SHA512,
                BCrypt,
                ARGON2ID
            )
        }

        fun fromKey(key: String): InputHasher = when (key) {
            KEY_SHA256 -> SHA256
            KEY_SHA512 -> SHA512
            KEY_BCRYPT -> BCrypt
            KEY_ARGON2_ID -> ARGON2ID
            else -> throw IllegalArgumentException("Unknown hashing key: $key")
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
