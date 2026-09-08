package ir.ornix.passgen.passwordgenerator.random

import kotlin.js.JsAny

actual fun secureRandomBytes(size: Int): ByteArray {
    require(size >= 0) { "size must be >= 0" }
    val result = createUint8Array(size)
    getRandomValues(result)
    return ByteArray(size) { getUint8ArrayValue(result, it).toByte() }
}

@JsFun("(size) => new Uint8Array(size)")
private external fun createUint8Array(size: Int): JsAny

@JsFun("(array) => crypto.getRandomValues(array)")
private external fun getRandomValues(array: JsAny)

@JsFun("(array, index) => array[index]")
private external fun getUint8ArrayValue(array: JsAny, index: Int): Int
