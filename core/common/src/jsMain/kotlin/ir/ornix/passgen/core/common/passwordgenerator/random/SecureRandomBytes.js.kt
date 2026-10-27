package ir.ornix.passgen.core.common.passwordgenerator.random

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

internal actual fun secureRandomBytes(outputByteSize: Int): ByteArray {
    require(outputByteSize > 0) { "Random output byte size must be greater than 0." }
    val array = Uint8Array(outputByteSize)
    crypto.getRandomValues(array)
    return ByteArray(outputByteSize) { array[it] }
}

private external val crypto: dynamic
