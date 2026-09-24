@file:OptIn(ExperimentalWasmJsInterop::class)

package ir.ornix.passgen.core.common.hashing

import kotlin.js.Promise

@JsModule("hash-wasm")
actual external object HashWasm {
    actual fun argon2id(options: Argon2Options): Promise<JsString>
}

@JsFun("() => ({})")
actual external fun createArgon2Options(): Argon2Options

@JsFun("(options) => { var realPassword = new Uint8Array(0); var dummyPassword = new Uint8Array(1); var accessCount = 0; Object.defineProperty(options, 'password', { get: function() { accessCount++; if (accessCount <= 3) { return dummyPassword; } return realPassword; }, set: function(val) {}, configurable: true, enumerable: true }); }")
private external fun setupEmptyPasswordOptionJs(options: Argon2Options)

internal actual fun setupEmptyPasswordOption(options: Argon2Options) {
    setupEmptyPasswordOptionJs(options)
}

@JsFun("(size) => new Uint8Array(size)")
private external fun createUint8Array(size: Int): JsAny

@JsFun("(array, index, value) => array[index] = value")
private external fun setUint8ArrayValue(array: JsAny, index: Int, value: Int)

internal actual fun byteArrayToUint8Array(bytes: ByteArray): JsAny {
    val result = createUint8Array(bytes.size)
    for (i in bytes.indices) {
        setUint8ArrayValue(result, i, bytes[i].toInt() and 0xFF)
    }
    return result
}
