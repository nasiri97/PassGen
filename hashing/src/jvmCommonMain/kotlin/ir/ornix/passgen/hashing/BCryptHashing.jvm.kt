package ir.ornix.passgen.hashing

import ir.ornix.passgen.hashing.core.Hashing
import org.bouncycastle.crypto.generators.BCrypt

/**
 * BCrypt password hashing.
 *
 * The standard BCrypt encoded format is:
 *
 *     $<2-char-version>$<2-digit-cost>$<22-char-salt><31-char-hash>
 *
 * Example:
 *
 *     $2a$12$JNHLsj8umu2k4BqovZlgleaWoo2BxGM0sAUbeJTsGdDCs24koovhq
 *
 * The encoded output is always 60 ASCII characters:
 *
 *     Version: 2 characters
 *     Cost:    2 digits
 *     Salt:    22 BCrypt Base64 characters     (16 bytes)
 *     Hash:    31 BCrypt Base64 characters     (23 bytes)
 *
 * BCrypt uses its own Base64 variant, commonly called BCrypt Base64,
 * whose alphabet differs from RFC 4648 Base64.
 *
 */
actual class BCryptHashing : Hashing {

    /**
     * Size of the raw BCrypt hash returned by this implementation.
     *
     * 23 raw bytes → 31 BCrypt Base64 characters.
     */
    override val outputByteSize = 23


    companion object {

        private val sha256Hashing = Sha256Hashing()

        // BCrypt Work-Factor
        private const val COST = 12

        /**
         * BCrypt traditionally processes the password with a terminating
         * NUL byte (0x00).
         *
         * Setting this to true makes BCrypt.generate() add that terminator
         * to the input before performing the BCrypt key setup.
         *
         * This should remain true when the goal is compatibility with the
         * conventional BCrypt password-hashing behavior.
         *
         * If this implementation is intended to reproduce a protocol that
         * explicitly defines the BCrypt input as raw bytes without the
         * terminator, this value must instead match that protocol.
         */
        private const val ADD_TERMINATOR = true
    }

    override suspend fun digest(input: ByteArray): ByteArray {

        /**
         * BCrypt requires a 16-byte salt
         *
         * The salt is deterministically derived from the input by hashing
         * the input with SHA-256 and taking the first 16 bytes.
         */
        val salt = sha256Hashing.digest(input).copyOfRange(0, 16)

        return digest(input, salt)
    }


    internal suspend fun digest(input: ByteArray, salt: ByteArray): ByteArray {

        /**
         * BCrypt.generate() returns the raw EksBlowfish-derived output as
         * 24 bytes (192 bits). The standard BCrypt encoded format uses only
         * the first 23 bytes of this output, which are encoded into 31
         * BCrypt Base64 characters.
         */
        val output = BCrypt.generate(input, salt, COST, ADD_TERMINATOR)

        // Keep the 23-byte BCrypt hash portion.
        // 23 bytes encode to 31 BCrypt Base64 characters.
        return output.copyOfRange(0, 23)
    }
}