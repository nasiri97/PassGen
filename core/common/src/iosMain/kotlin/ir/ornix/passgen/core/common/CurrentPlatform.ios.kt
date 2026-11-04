package ir.ornix.passgen.core.common

actual val platform: Platform
    get() = Platform.Ios(
        isDebugBuild = false
    )