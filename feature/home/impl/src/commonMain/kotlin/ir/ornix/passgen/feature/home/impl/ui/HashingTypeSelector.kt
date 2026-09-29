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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher

@Composable
fun HashingTypeSelector(
    selected: InputHasher?,
    onSelected: (InputHasher) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier) {
        InputHasher.allItems.forEach { inputHasher ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelected(inputHasher) }
            ) {
                RadioButton(
                    selected = selected == inputHasher,
                    onClick = { onSelected(inputHasher) }
                )
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(inputHasher.fullName)
                        }

                        inputHasher.description?.let {
                            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                append(" $it")
                            }
                        }
                    }
                )
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