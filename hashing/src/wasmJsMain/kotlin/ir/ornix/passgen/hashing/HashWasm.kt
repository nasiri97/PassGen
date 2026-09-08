package ir.ornix.passgen.hashing

import kotlin.js.Promise
import kotlin.js.JsAny
import kotlin.js.JsString

@JsModule("hash-wasm")
external object HashWasm {
    fun argon2id(options: Argon2Options): Promise<JsString>
    fun bcrypt(options: BcryptOptions): Promise<JsString>
}

external interface Argon2Options : JsAny {
    var password: JsAny 
    var salt: JsAny 
    var iterations: Int
    var memorySize: Int
    var parallelism: Int
    var hashLength: Int
    var outputType: String
}

external interface BcryptOptions : JsAny {
    var password: JsAny 
    var salt: JsAny 
    var cost: Int
    var outputType: String
}

@JsFun("() => ({})")
internal external fun createArgon2Options(): Argon2Options

@JsFun("() => ({})")
internal external fun createBcryptOptions(): BcryptOptions
