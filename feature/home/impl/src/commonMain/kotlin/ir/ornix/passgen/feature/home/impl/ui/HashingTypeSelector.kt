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
import ir.ornix.passgen.passwordgenerator.model.InputHasher

@Composable
fun HashingTypeSelector(
    selected: InputHasher?,
    onSelected: (InputHasher) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier) {
        InputHasher.items.forEach { config ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelected(config) }
            ) {
                RadioButton(
                    selected = selected == config,
                    onClick = { onSelected(config) }
                )
                Text(config.key)
            }
        }
    }
}


@Composable
@Preview
fun HashingTypeSelectorPreview() {
    HashingTypeSelector(
        selected = null,
        onSelected = { }
    )
}