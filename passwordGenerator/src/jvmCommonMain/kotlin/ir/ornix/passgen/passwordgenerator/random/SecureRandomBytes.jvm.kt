package ir.ornix.passgen.passwordgenerator.random

import java.security.SecureRandom

private val secureRandom = SecureRandom()

actual fun secureRandomBytes(size: Int): ByteArray {
    require(size >= 0) { "size must be >= 0" }
    val bytes = ByteArray(size)
    secureRandom.nextBytes(bytes)
    return bytes
}
