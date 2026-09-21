package ir.ornix.passgen.core.common


var isIosDebugBuild = false

actual val isDebugBuild: Boolean
    get() = isIosDebugBuild