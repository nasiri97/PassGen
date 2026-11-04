package ir.ornix.passgen.core.common

actual val platform: Platform
    get() = Platform.Desktop(
        isDebugBuild = System.getProperty("app.debug") == "true"
    )