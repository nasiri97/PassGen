package ir.ornix.passgen.core.common.passwordgenerator.random

import java.security.SecureRandom

private val secureRandom = SecureRandom()

internal actual fun secureRandomBytes(outputByteSize: Int): ByteArray {
    require(outputByteSize > 0) { "Random output byte size must be greater than 0." }
    val bytes = ByteArray(outputByteSize)
    secureRandom.nextBytes(bytes)
    return bytes
}
