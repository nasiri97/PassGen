package ir.ornix.passgen.feature.auth.impl.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

@Composable
fun PatternLockView(
    onPatternCompleted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dotColor = MaterialTheme.colorScheme.outline
    val activeColor = MaterialTheme.colorScheme.primary
    val lineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)

    var selectedDots by remember { mutableStateOf(emptyList<Int>()) }
    var currentDragPosition by remember { mutableStateOf<Offset?>(null) }
    var gridCenters by remember { mutableStateOf(emptyList<Offset>()) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        selectedDots = emptyList()
                        currentDragPosition = offset

                        gridCenters.forEachIndexed { index, center ->
                            if (isPointNearDot(offset, center, 40.dp.toPx())) {
                                selectedDots = listOf(index)
                            }
                        }
                    },
                    onDrag = { change, _ ->
                        val position = change.position
                        currentDragPosition = position

                        gridCenters.forEachIndexed { index, center ->
                            if (isPointNearDot(
                                    position,
                                    center,
                                    40.dp.toPx()
                                ) && index !in selectedDots
                            ) {
                                selectedDots = selectedDots + index
                            }
                        }
                    },
                    onDragEnd = {
                        if (selectedDots.isNotEmpty()) {
                            onPatternCompleted(selectedDots.joinToString(","))
                        }
                        selectedDots = emptyList()
                        currentDragPosition = null
                    },
                    onDragCancel = {
                        selectedDots = emptyList()
                        currentDragPosition = null
                    }
                )
            }
    ) {
        val width = size.width
        val height = size.height
        val cellWidth = width / 3f
        val cellHeight = height / 3f

        if (gridCenters.isEmpty()) {
            val centers = mutableListOf<Offset>()
            for (row in 0 until 3) {
                for (col in 0 until 3) {
                    centers.add(
                        Offset(
                            x = cellWidth * col + cellWidth / 2f,
                            y = cellHeight * row + cellHeight / 2f
                        )
                    )
                }
            }
            gridCenters = centers
        }

        // Draw Lines between active dots
        if (selectedDots.isNotEmpty()) {
            for (i in 0 until selectedDots.size - 1) {
                val start = gridCenters[selectedDots[i]]
                val end = gridCenters[selectedDots[i + 1]]
                drawLine(
                    color = lineColor,
                    start = start,
                    end = end,
                    strokeWidth = 8.dp.toPx()
                )
            }

            // Line to current drag pointer
            currentDragPosition?.let { pos ->
                drawLine(
                    color = lineColor,
                    start = gridCenters[selectedDots.last()],
                    end = pos,
                    strokeWidth = 6.dp.toPx()
                )
            }
        }

        // Draw dots
        gridCenters.forEachIndexed { index, center ->
            val isActive = index in selectedDots
            drawCircle(
                color = if (isActive) activeColor else dotColor,
                radius = if (isActive) 14.dp.toPx() else 8.dp.toPx(),
                center = center
            )
            if (isActive) {
                drawCircle(
                    color = activeColor.copy(alpha = 0.2f),
                    radius = 24.dp.toPx(),
                    center = center
                )
            }
        }
    }
}

private fun isPointNearDot(point: Offset, dot: Offset, radius: Float): Boolean {
    val dx = point.x - dot.x
    val dy = point.y - dot.y
    return sqrt((dx * dx + dy * dy).toDouble()) <= radius
}
