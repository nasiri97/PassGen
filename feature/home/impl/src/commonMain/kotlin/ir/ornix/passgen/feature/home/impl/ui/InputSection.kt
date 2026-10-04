package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_clear
import ir.ornix.passgen.core.ui.input_phrase_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun InputSection(
    input: String,
    onInputChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = input,
        onValueChange = {
            if (!it.contains('\n')) onInputChange(it)
        },
        label = { Text(stringResource(Res.string.input_phrase_label)) },
        leadingIcon = {
            Icon(Icons.Rounded.Key, contentDescription = null)
        },
        trailingIcon = {
            if (input.isNotEmpty()) {
                IconButton(onClick = { onInputChange("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = stringResource(Res.string.action_clear)
                    )
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() })
    )
}
