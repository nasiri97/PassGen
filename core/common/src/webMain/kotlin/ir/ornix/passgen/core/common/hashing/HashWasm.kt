@file:OptIn(ExperimentalWasmJsInterop::class)

package ir.ornix.passgen.core.common.hashing

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsString
import kotlin.js.Promise

expect object HashWasm {
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
    var costFactor: Int
    var outputType: String
}

internal expect fun setupEmptyPasswordOption(options: Argon2Options): Unit
internal expect fun createArgon2Options(): Argon2Options
internal expect fun createBcryptOptions(): BcryptOptions
internal expect fun byteArrayToUint8Array(bytes: ByteArray): JsAny