package ir.ornix.passgen.core.common

actual val platform: Platform
    get() = Platform.Web(
        engine = WebEngine.JS,
        isDebugBuild = js("globalThis.__APP_DEBUG__ === true")
    )