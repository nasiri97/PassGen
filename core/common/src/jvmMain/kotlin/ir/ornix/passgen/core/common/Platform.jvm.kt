package ir.ornix.passgen.core.common

actual val isDebugBuild: Boolean
    get() = System.getProperty("app.debug") == "true"

actual val isGestureRefreshSupported: Boolean = false