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
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder

@Composable
fun EncoderTypeSelector(
    encoders: List<PassEncoder>,
    selectedEncoder: PassEncoder,
    onSelected: (PassEncoder) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        encoders.forEach { encoder ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelected(encoder) }
            ) {
                RadioButton(
                    selected = selectedEncoder == encoder,
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
        encoders = PassEncoder.getValidItems(64),
        selectedEncoder = StringPassEncoder.Z85PassEncoder,
        onSelected = {},
    )
}