@file:OptIn(ExperimentalWasmJsInterop::class)

package ir.ornix.passgen.hashing

import kotlin.js.JsAny
import kotlin.js.JsModule
import kotlin.js.JsString
import kotlin.js.Promise

@JsModule("hash-wasm")
actual external object HashWasm {
    actual fun argon2id(options: Argon2Options): Promise<JsString>
    actual fun bcrypt(options: BcryptOptions): Promise<JsString>
}

@JsFun("() => ({})")
internal actual external fun createArgon2Options(): Argon2Options

@JsFun("() => ({})")
internal actual external fun createBcryptOptions(): BcryptOptions

@JsFun("(size) => new Uint8Array(size)")
private external fun createUint8Array(size: Int): JsAny

@JsFun("(array, index, value) => array[index] = value")
private external fun setUint8ArrayValue(array: JsAny, index: Int, value: Int)

internal actual fun ByteArray.toUint8Array(): JsAny {
    val result = createUint8Array(this.size)
    for (i in this.indices) {
        setUint8ArrayValue(result, i, this[i].toInt() and 0xFF)
    }
    return result
}
