package ir.ornix.passgen.core.common.passwordgenerator.random

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.Security.SecRandomCopyBytes
import platform.Security.kSecRandomDefault

@OptIn(ExperimentalForeignApi::class)
actual fun secureRandomBytes(outputByteSize: Int): ByteArray {
    require(outputByteSize > 0) { "Random output byte size must be greater than 0." }
    val bytes = ByteArray(outputByteSize)
    if (outputByteSize == 0) return bytes
    val status = bytes.usePinned { pinned ->
        SecRandomCopyBytes(kSecRandomDefault, outputByteSize.convert(), pinned.addressOf(0))
    }
    check(status == 0) { "SecRandomCopyBytes failed with status $status" }
    return bytes
}