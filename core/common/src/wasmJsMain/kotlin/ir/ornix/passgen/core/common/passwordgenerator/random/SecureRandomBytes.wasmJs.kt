package ir.ornix.passgen.core.common.passwordgenerator.random

import kotlin.js.JsAny

internal actual fun secureRandomBytes(outputByteSize: Int): ByteArray {
    require(outputByteSize > 0) { "Random output byte size must be greater than 0." }
    val result = createUint8Array(outputByteSize)
    getRandomValues(result)
    return ByteArray(outputByteSize) { getUint8ArrayValue(result, it).toByte() }
}

@JsFun("(size) => new Uint8Array(size)")
private external fun createUint8Array(size: Int): JsAny

@JsFun("(array) => crypto.getRandomValues(array)")
private external fun getRandomValues(array: JsAny)

@JsFun("(array, index) => array[index]")
private external fun getUint8ArrayValue(array: JsAny, index: Int): Int
