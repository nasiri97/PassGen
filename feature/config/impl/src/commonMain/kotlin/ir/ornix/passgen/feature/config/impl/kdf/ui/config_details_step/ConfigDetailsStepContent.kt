package ir.ornix.passgen.feature.config.impl.kdf.ui.config_details_step

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_back
import ir.ornix.passgen.core.ui.action_create
import ir.ornix.passgen.core.ui.action_creating
import ir.ornix.passgen.core.ui.component.EncoderTypeSelector
import ir.ornix.passgen.core.ui.component.HashingTypeSelector
import ir.ornix.passgen.core.ui.component.NumberSlider
import ir.ornix.passgen.core.ui.label_approx_bytes_format
import ir.ornix.passgen.core.ui.label_config_name
import ir.ornix.passgen.core.ui.label_password_length_format
import ir.ornix.passgen.core.ui.option_collapse_spaces
import ir.ornix.passgen.core.ui.option_convert_lowercase
import ir.ornix.passgen.core.ui.option_trim_spaces
import ir.ornix.passgen.core.ui.title_config_details
import ir.ornix.passgen.core.ui.title_preprocessing
import ir.ornix.passgen.feature.config.impl.kdf.presentation.AddKdsConfigUiState
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ConfigDetailsStepContent(
    uiState: AddKdsConfigUiState,
    availableEncoders: List<PassEncoder>,
    maxAvailablePassLength: Int?,
    entropyByteSize: Int,
    onNameChange: (String) -> Unit,
    onTrimSpacesChange: (Boolean) -> Unit,
    onCollapseSpacesChange: (Boolean) -> Unit,
    onLowercaseChange: (Boolean) -> Unit,
    onHasherSelected: (InputHasher) -> Unit,
    onEncoderSelected: (PassEncoder) -> Unit,
    onPassLengthChange: (Int) -> Unit,
    onBack: () -> Unit,
    onCreate: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(Res.string.title_config_details),
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(Res.string.label_config_name)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("config_name_input")
        )

        HorizontalDivider()

        Preprocessing(
            trimSpaces = uiState.trimSpaces,
            collapseSpaces = uiState.collapseSpaces,
            lowercase = uiState.lowercase,
            trimSpacesChanged = onTrimSpacesChange,
            collapseSpacesChanged = onCollapseSpacesChange,
            lowercaseChanged = onLowercaseChange
        )

        HorizontalDivider()

        HashingTypeSelector(
            selected = uiState.selectedHasher,
            onSelected = onHasherSelected
        )

        HorizontalDivider()

        EncoderTypeSelector(
            encoders = availableEncoders,
            selectedEncoder = uiState.selectedEncoder,
            onSelected = onEncoderSelected
        )

        HorizontalDivider()

        if (uiState.selectedEncoder is StringPassEncoder && maxAvailablePassLength != null) {
            PassLengthSection(
                passLength = uiState.passLength,
                entropyByteSize = entropyByteSize,
                maxAvailablePassLength = maxAvailablePassLength,
                onPassLengthChange = onPassLengthChange
            )
        }

        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onBack,
                enabled = !uiState.isSubmitting
            ) {
                Text(stringResource(Res.string.action_back))
            }

            Spacer(Modifier.width(8.dp))

            Button(
                modifier = Modifier.testTag("submit_button"),
                enabled = uiState.name.isNotBlank() && !uiState.isSubmitting,
                onClick = onCreate
            ) {
                Text(
                    if (uiState.isSubmitting) stringResource(Res.string.action_creating) else stringResource(
                        Res.string.action_create
                    )
                )
            }
        }
    }
}

@Composable
private fun Preprocessing(
    trimSpaces: Boolean,
    collapseSpaces: Boolean,
    lowercase: Boolean,
    trimSpacesChanged: (Boolean) -> Unit,
    collapseSpacesChanged: (Boolean) -> Unit,
    lowercaseChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            stringResource(Res.string.title_preprocessing),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        LabeledCheckbox(
            label = stringResource(Res.string.option_trim_spaces),
            checked = trimSpaces,
            onCheckedChange = trimSpacesChanged
        )

        LabeledCheckbox(
            label = stringResource(Res.string.option_collapse_spaces),
            checked = collapseSpaces,
            onCheckedChange = collapseSpacesChanged
        )

        LabeledCheckbox(
            label = stringResource(Res.string.option_convert_lowercase),
            checked = lowercase,
            onCheckedChange = lowercaseChanged
        )
    }
}

@Composable
private fun PassLengthSection(
    passLength: Int,
    maxAvailablePassLength: Int,
    entropyByteSize: Int,
    onPassLengthChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row {
            Text(
                text = stringResource(Res.string.label_password_length_format, passLength),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                text = stringResource(Res.string.label_approx_bytes_format, entropyByteSize),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        NumberSlider(
            currentValue = passLength,
            min = 8,
            max = maxAvailablePassLength,
            onValueChange = onPassLengthChange
        )
    }
}

@Composable
private fun LabeledCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = { onCheckedChange(!checked) })
    ) {
        Checkbox(
            modifier = Modifier.testTag(label),
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}