package ir.ornix.passgen.passwordgenerator.random

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

actual fun secureRandomBytes(size: Int): ByteArray {
    require(size >= 0) { "size must be >= 0" }
    val array = Uint8Array(size)
    getRandomValues(array)
    return ByteArray(size) { array[it] }
}

private fun getRandomValues(array: Uint8Array) {
    js("crypto.getRandomValues(array)")
}