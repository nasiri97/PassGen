package ir.ornix.passgen.core.designsystem.component

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass.Companion.COMPACT
import androidx.window.core.layout.WindowWidthSizeClass.Companion.EXPANDED
import androidx.window.core.layout.WindowWidthSizeClass.Companion.MEDIUM

@Composable
fun AdaptiveComponent(
    compactComponent: @Composable () -> Unit,
    expandedComponent: (@Composable () -> Unit)?
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    when (windowSizeClass.windowWidthSizeClass) {
        COMPACT -> compactComponent()

        MEDIUM, EXPANDED -> {
            expandedComponent?.invoke() ?: compactComponent()
        }
    }
}


@Composable
fun <T> getAdaptiveValue(
    compact: () -> T,
    expanded: () -> T
): T {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    return when (windowSizeClass.windowWidthSizeClass) {
        COMPACT -> compact()
        else -> expanded.invoke()
    }
}
