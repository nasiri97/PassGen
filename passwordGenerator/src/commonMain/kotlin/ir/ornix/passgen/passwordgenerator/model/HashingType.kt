package ir.ornix.passgen.passwordgenerator.model

import ir.ornix.passgen.hashing.Argon2Hashing
import ir.ornix.passgen.hashing.BCryptHashing
import ir.ornix.passgen.hashing.Sha256Hashing
import ir.ornix.passgen.hashing.Sha512Hashing
import ir.ornix.passgen.hashing.core.Hashing
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Base class for all hashing algorithm configurations.
 * Each subclass defines a unique [key] and can create its corresponding [Hashing][ir.ornix.passgen.hashing.core.Hashing] instance.
 */
@Serializable(with = HashingTypeSerializer::class)
sealed class HashingType(override val key: String) : KeyBasedType<Hashing>() {

    object SHA256 : HashingType(KEY_SHA256) {
        override fun createInstance(): Hashing = Sha256Hashing()
    }

    object SHA512 : HashingType(KEY_SHA512) {
        override fun createInstance(): Hashing = Sha512Hashing()
    }

    object BCrypt : HashingType(KEY_BCRYPT) {
        override fun createInstance(): Hashing = BCryptHashing()
    }

    object ARGON2ID : HashingType(KEY_ARGON2_ID) {
        override fun createInstance(): Hashing = Argon2Hashing
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

        fun fromKey(key: String): HashingType = when (key) {
            KEY_SHA256 -> SHA256
            KEY_SHA512 -> SHA512
            KEY_BCRYPT -> BCrypt
            KEY_ARGON2_ID -> ARGON2ID
            else -> throw IllegalArgumentException("Unknown hashing key: $key")
        }
    }
}

object HashingTypeSerializer : KSerializer<HashingType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("HashingType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: HashingType) = encoder.encodeString(value.key)
    override fun deserialize(decoder: Decoder): HashingType =
        HashingType.fromKey(decoder.decodeString())
}
