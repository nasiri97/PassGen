package ir.ornix.passgen.core.common

sealed interface Platform {
    val isDebugBuild: Boolean
    val isGestureRefreshSupported: Boolean

    data class Android(
        val apiLevel: Int,
        override val isDebugBuild: Boolean,
        override val isGestureRefreshSupported: Boolean = true
    ) : Platform

    data class Ios(
        override val isDebugBuild: Boolean,
        override val isGestureRefreshSupported: Boolean = true
    ) : Platform

    data class Desktop(
        override val isDebugBuild: Boolean,
        override val isGestureRefreshSupported: Boolean = false
    ) : Platform

    data class Web(
        val engine: WebEngine,
        override val isDebugBuild: Boolean,
        override val isGestureRefreshSupported: Boolean = false
    ) : Platform
}

enum class WebEngine {
    JS,
    WASM
}