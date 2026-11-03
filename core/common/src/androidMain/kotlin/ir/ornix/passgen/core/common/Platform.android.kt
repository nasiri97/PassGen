package ir.ornix.passgen.core.common


var isAndroidDebugBuild = false

actual val isDebugBuild: Boolean
    get() = isAndroidDebugBuild

actual val isGestureRefreshSupported: Boolean = true