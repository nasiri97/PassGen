package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ir.ornix.passgen.passwordgenerator.model.PassEncoder

@Composable
fun EncoderTypeSelector(
    selected: PassEncoder?,
    onSelected: (PassEncoder) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier) {
        PassEncoder.items.forEach { encoder ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelected(encoder) }
            ) {
                RadioButton(
                    selected = selected == encoder,
                    onClick = { onSelected(encoder) }
                )
                Text(encoder.key)
            }
        }
    }
}


@Composable
@Preview
fun EncoderTypeSelectorPreview() {
    EncoderTypeSelector(
        selected = null,
        onSelected = {},
    )
}