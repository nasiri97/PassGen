package ir.ornix.passgen.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PullToRefreshWithHint(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    hasHintShown: Boolean,
    onHintShown: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        content()

        if (!hasHintShown) {
            PullToRefreshHint(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                onHintShown = onHintShown
            )
        }
    }
}

@Composable
private fun PullToRefreshHint(
    onHintShown: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(true) }
    val offsetY = remember { Animatable(0f) }
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        val firstPull = with(density) { 80.dp.toPx() }
        val secondPull = with(density) { 48.dp.toPx() }

        offsetY.animateTo(
            targetValue = firstPull,
            animationSpec = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing,
            )
        )

        delay(120.milliseconds)

        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            )
        )

        delay(150.milliseconds)

        offsetY.animateTo(
            targetValue = secondPull,
            animationSpec = tween(
                durationMillis = 450,
                easing = FastOutSlowInEasing,
            )
        )

        delay(80.milliseconds)

        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            )
        )

        visible = false
        onHintShown()
    }

    AnimatedVisibility(
        modifier = modifier.fillMaxWidth(),
        visible = visible,
        exit = fadeOut(
            animationSpec = tween(350),
        ),
    ) {
        Surface(
            modifier = Modifier.wrapContentSize().offset {
                IntOffset(
                    x = 0,
                    y = offsetY.value.roundToInt(),
                )
            },
            shape = RoundedCornerShape(48.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.8f),
            tonalElevation = 3.dp,
            shadowElevation = 2.dp,
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                Text(
                    text = "Pull down to refresh",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}