package ir.ornix.passgen.core.common

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("() => globalThis.__APP_DEBUG__ === true")
private external fun getDebugBuild(): Boolean

actual val isDebugBuild: Boolean
    get() = getDebugBuild()