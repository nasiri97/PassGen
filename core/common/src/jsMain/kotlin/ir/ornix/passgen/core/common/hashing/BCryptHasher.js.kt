@file:OptIn(ExperimentalWasmJsInterop::class)

package ir.ornix.passgen.core.common.hashing

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.set
import kotlin.js.Promise

@JsModule("hash-wasm")
@JsNonModule
actual external object HashWasm {
    actual fun argon2id(options: Argon2Options): Promise<JsString>
    actual fun bcrypt(options: BcryptOptions): Promise<JsString>
}

internal actual fun createArgon2Options(): Argon2Options = js("({})").unsafeCast<Argon2Options>()
internal actual fun createBcryptOptions(): BcryptOptions = js("({})").unsafeCast<BcryptOptions>()

internal actual fun setupEmptyPasswordOption(options: Argon2Options) {
    js("""
        var realPassword = new Uint8Array(0);
        var dummyPassword = new Uint8Array(1);
        var accessCount = 0;
        Object.defineProperty(options, 'password', {
            get: function() {
                accessCount++;
                if (accessCount <= 3) {
                    return dummyPassword;
                }
                return realPassword;
            },
            set: function(val) {},
            configurable: true,
            enumerable: true
        });
    """)
}

internal actual fun byteArrayToUint8Array(bytes: ByteArray): JsAny {
    val result = Uint8Array(bytes.size)
    for (i in bytes.indices) {
        result[i] = bytes[i]
    }
    return result.unsafeCast<JsAny>()
}
