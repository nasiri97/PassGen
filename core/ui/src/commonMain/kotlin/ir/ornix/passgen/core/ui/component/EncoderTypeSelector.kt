package ir.ornix.passgen.core.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncoderTypeSelector(
    encoders: List<PassEncoder>,
    selectedEncoder: PassEncoder,
    onSelected: (PassEncoder) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedEncoder.fullName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Encoder Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            modifier = Modifier.testTag("encoder_type_selector"),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            encoders.forEach { passEncoder ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = passEncoder.fullName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (passEncoder == selectedEncoder) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelected(passEncoder)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
