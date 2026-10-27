package ir.ornix.passgen.core.common


var isAndroidDebugBuild = false

actual val isDebugBuild: Boolean
    get() = isAndroidDebugBuild