@file:OptIn(ExperimentalWasmJsInterop::class)

package ir.ornix.passgen.hashing

import kotlin.js.JsAny
import kotlin.js.JsModule
import kotlin.js.JsNonModule
import kotlin.js.JsString
import kotlin.js.Promise
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.set

@JsModule("hash-wasm")
@JsNonModule
actual external object HashWasm {
    actual fun argon2id(options: Argon2Options): Promise<JsString>
    actual fun bcrypt(options: BcryptOptions): Promise<JsString>
}

internal actual fun createArgon2Options(): Argon2Options = js("({})").unsafeCast<Argon2Options>()
internal actual fun createBcryptOptions(): BcryptOptions = js("({})").unsafeCast<BcryptOptions>()

internal actual fun ByteArray.toUint8Array(): JsAny {
    val result = Uint8Array(this.size)
    for (i in this.indices) {
        result[i] = this[i]
    }
    return result.unsafeCast<JsAny>()
}
