package ir.ornix.passgen.hashing

import kotlin.js.Promise

@JsModule("hash-wasm")
@JsNonModule
external object HashWasm {
    fun argon2id(options: Argon2Options): Promise<String>
    fun bcrypt(options: BcryptOptions): Promise<String>
}

external interface Argon2Options {
    var password: Any 
    var salt: Any 
    var iterations: Int
    var memorySize: Int
    var parallelism: Int
    var hashLength: Int
    var outputType: String
}

external interface BcryptOptions {
    var password: Any 
    var salt: Any 
    var cost: Int
    var outputType: String
}

internal fun createArgon2Options(): Argon2Options = js("({})")
internal fun createBcryptOptions(): BcryptOptions = js("({})")
