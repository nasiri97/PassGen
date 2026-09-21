package ir.ornix.passgen.core.common

actual val isDebugBuild: Boolean
    get() = js("globalThis.__APP_DEBUG__ === true")