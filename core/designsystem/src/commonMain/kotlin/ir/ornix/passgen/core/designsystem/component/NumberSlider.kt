package ir.ornix.passgen.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import ir.ornix.passgen.core.designsystem.BothPreview


@Composable
fun NumberSlider(
    currentValue: Int,
    min: Int,
    max: Int,
    onValueChange: (newValue: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = max - min

    Column(modifier) {
        Slider(
            value = currentValue.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = min.toFloat()..max.toFloat(),
            steps = steps,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                text = "$min",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = "${min + steps / 3}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = "${max - steps / 3}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                text = "$max",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@BothPreview
@Composable
fun NumberSliderPreview() {
    NumberSlider(
        currentValue = 12,
        min = 8,
        max = 64,
        onValueChange = {},
    )
}