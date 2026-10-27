package ir.ornix.passgen.core.ui.passgen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

private const val TILE_COUNT = 8
private const val CHAR_POOL =
    "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-:+=^!/*?&<>()[]{}@%$#"

private fun randomChars(count: Int): String =
    buildString { repeat(count) { append(CHAR_POOL[Random.nextInt(CHAR_POOL.length)]) } }

/** Holds the indicator's state. Survives scrolling out of a LazyColumn. */
@Stable
class PasswordGeneratingState(initialChars: String = randomChars(TILE_COUNT)) {
    var chars by mutableStateOf(initialChars)
        private set

    internal fun shuffle() {
        chars = randomChars(chars.length)
    }

    companion object {
        // Saved as a plain String, which every platform's saver supports
        val Saver = androidx.compose.runtime.saveable.Saver<PasswordGeneratingState, String>(
            save = { it.chars },
            restore = { PasswordGeneratingState(it) }
        )
    }
}

@Composable
fun rememberPasswordGeneratingState(): PasswordGeneratingState =
    rememberSaveable(saver = PasswordGeneratingState.Saver) { PasswordGeneratingState() }

@Composable
fun PasswordGeneratingIndicator(
    modifier: Modifier = Modifier,
    state: PasswordGeneratingState = rememberPasswordGeneratingState(),
) {
    val colors = MaterialTheme.colorScheme

    // Tile (container, content) pairs, rebuilt only when the theme changes
    val tilePalette = remember(colors) {
        listOf(
            colors.primaryContainer to colors.onPrimaryContainer,
            colors.secondaryContainer to colors.onSecondaryContainer,
            colors.tertiaryContainer to colors.onTertiaryContainer
        )
    }

    // Scramble characters
    LaunchedEffect(state) {
        while (true) {
            delay(500.milliseconds)
            state.shuffle()
        }
    }

    val transition = rememberInfiniteTransition(label = "password-generating")
    val wave by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1300, easing = LinearEasing)),
        label = "wave"
    )

    Box(
        modifier = modifier
            .background(Brush.verticalGradient(listOf(colors.surface, colors.surfaceVariant))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                state.chars.forEachIndexed { index, char ->
                    val phase = (wave + index / TILE_COUNT.toFloat()) * 2f * PI.toFloat()
                    val bounce = sin(phase)
                    val (container, content) = tilePalette[index % tilePalette.size]
                    val rotation = bounce * 12f // Subtle tilt back and forth
                    val scale = 1f + 0.06f * bounce

                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                translationY = -bounce * 8.dp.toPx()
                                scaleX = scale
                                scaleY = scale
                            }
                            .size(width = 30.dp, height = 30.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(container),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.graphicsLayer {
                                rotationZ = rotation
                            },
                            text = char.toString(),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = content
                        )
                    }
                }
            }
        }
    }
}