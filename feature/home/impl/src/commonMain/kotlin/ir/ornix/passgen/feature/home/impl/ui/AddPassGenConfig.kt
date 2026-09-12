package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ir.ornix.passgen.core.designsystem.component.NumberSlider
import ir.ornix.passgen.core.domain.PreprocessConfig
import ir.ornix.passgen.core.domain.core.KDFPassGenConfig
import ir.ornix.passgen.feature.home.impl.ui.utils.InputHasherSaver
import ir.ornix.passgen.feature.home.impl.ui.utils.PassEncoderSaver
import ir.ornix.passgen.passwordgenerator.model.InputHasher
import ir.ornix.passgen.passwordgenerator.model.PassEncoder

@Composable
fun AddPassGenConfig(
    onSubmit: (KDFPassGenConfig) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 4.dp
        ) {
            AddPassGenConfigContent(
                onSubmit = onSubmit,
                onCancel = onDismissRequest
            )
        }
    }
}

@Composable
private fun AddPassGenConfigContent(
    onSubmit: (KDFPassGenConfig) -> Unit,
    onCancel: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }

    var trimSpaces by rememberSaveable { mutableStateOf(true) }
    var collapseSpaces by rememberSaveable { mutableStateOf(true) }
    var lowercase by rememberSaveable { mutableStateOf(false) }

    var selectedHashing by rememberSaveable(stateSaver = InputHasherSaver) {
        mutableStateOf(InputHasher.SHA256)
    }

    var selectedEncoder by rememberSaveable(stateSaver = PassEncoderSaver) {
        mutableStateOf(PassEncoder.HexPassEncoder)
    }

    val tokenUnitCount by remember(selectedHashing, selectedEncoder) {
        derivedStateOf {
            selectedEncoder.getTokenLength(hasher = selectedHashing.hasher)
        }
    }

    var passLength by rememberSaveable { mutableIntStateOf(64) }

    LaunchedEffect(tokenUnitCount) {
        if (passLength > tokenUnitCount) passLength = tokenUnitCount
    }

    val approximateByteCount by remember(selectedEncoder, passLength) {
        derivedStateOf {
            selectedEncoder.approximateDecodedSize(passLength)
        }
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Create Password Config",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Configuration Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        Preprocessing(
            trimSpaces = trimSpaces,
            collapseSpaces = collapseSpaces,
            lowercase = lowercase,
            trimSpacesChanged = { trimSpaces = it },
            collapseSpacesChanged = { collapseSpaces = it },
            lowercaseChanged = { lowercase = it }
        )

        HorizontalDivider()

        Text(
            "Hashing Algorithm",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        HashingTypeSelector(
            selected = selectedHashing,
            onSelected = { selectedHashing = it }
        )

        HorizontalDivider()

        Text(
            "Encoder Type",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        EncoderTypeSelector(
            selected = selectedEncoder,
            onSelected = { selectedEncoder = it }
        )

        HorizontalDivider()

        PassLengthSection(
            passLength = passLength,
            approximateByteCount = approximateByteCount,
            tokenUnitCount = tokenUnitCount,
            onPassLengthChange = { passLength = it }
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }

            Spacer(Modifier.width(8.dp))

            Button(
                enabled = name.isNotBlank(),
                onClick = {
                    onSubmit(
                        KDFPassGenConfig(
                            id = 0, // ID will be handled by logic/DB
                            name = name.trim(),
                            passEncoder = selectedEncoder,
                            passwordLength = passLength,
                            preprocessConfig = PreprocessConfig(
                                trimLeadingAndTrailingSpaces = trimSpaces,
                                collapseMultipleSpaces = collapseSpaces,
                                convertToLowercase = lowercase
                            ),
                            inputHasher = selectedHashing
                        )
                    )
                }
            ) {
                Text("Create")
            }
        }
    }
}


@Composable
fun Preprocessing(
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
            "Preprocessing",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        LabeledCheckbox(
            label = "Trim leading/trailing spaces",
            checked = trimSpaces,
            onCheckedChange = { trimSpacesChanged(it) }
        )

        LabeledCheckbox(
            label = "Collapse multiple spaces",
            checked = collapseSpaces,
            onCheckedChange = { collapseSpacesChanged(it) }
        )

        LabeledCheckbox(
            label = "Convert to lowercase",
            checked = lowercase,
            onCheckedChange = { lowercaseChanged(it) }
        )
    }
}

@Composable
fun PassLengthSection(
    passLength: Int,
    tokenUnitCount: Int,
    approximateByteCount: Int,
    onPassLengthChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row {
            Text(
                text = "Password length: $passLength",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                text = "≈ $approximateByteCount bytes",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        NumberSlider(
            currentValue = passLength,
            min = 8,
            max = tokenUnitCount,
            onValueChange = { onPassLengthChange(it) })
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
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}
