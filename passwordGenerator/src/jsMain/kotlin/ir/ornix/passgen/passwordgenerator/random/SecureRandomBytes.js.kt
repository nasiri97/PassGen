package ir.ornix.passgen.passwordgenerator.random

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

internal actual fun secureRandomBytes(size: Int): ByteArray {
    require(size >= 0) { "size must be >= 0" }
    val array = Uint8Array(size)
    crypto.getRandomValues(array)
    return ByteArray(size) { array[it] }
}

private external val crypto: dynamic
