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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder.Base64PassEncoder.approximateDecodedSize
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder.Base64PassEncoder.getTokenLength
import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import ir.ornix.passgen.core.domain.passgenconfig.model.PreprocessConfig
import ir.ornix.passgen.core.ui.component.NumberSlider
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.feature.home.impl.ui.utils.InputHasherSaver
import ir.ornix.passgen.feature.home.impl.ui.utils.PassEncoderSaver

@Composable
fun AddPassGenConfig(
    onSubmit: (KDFPassGenConfig, ByteArray) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier.secureContent()
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
    onSubmit: (KDFPassGenConfig, ByteArray) -> Unit,
    onCancel: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var masterKey by remember { mutableStateOf("") }

    var trimSpaces by rememberSaveable { mutableStateOf(true) }
    var collapseSpaces by rememberSaveable { mutableStateOf(true) }
    var lowercase by rememberSaveable { mutableStateOf(false) }

    var selectedHasher by rememberSaveable(stateSaver = InputHasherSaver) {
        mutableStateOf(InputHasher.SHA256)
    }


    val encoders = PassEncoder.getValidItems(selectedHasher)

    var selectedEncoder by rememberSaveable(stateSaver = PassEncoderSaver) {
        mutableStateOf<PassEncoder>(StringPassEncoder.HexPassEncoder)
    }

    LaunchedEffect(encoders) {
        if (!encoders.contains(selectedEncoder))
            selectedEncoder = encoders.first()
    }

    val tokenUnitCount by remember(selectedHasher, selectedEncoder) {
        derivedStateOf {
            when (selectedEncoder) {
                is SeedPassEncoder -> null
                is StringPassEncoder -> getTokenLength(inputHasher = selectedHasher)
            }
        }
    }


    var passLength by rememberSaveable { mutableStateOf(64) }

    LaunchedEffect(selectedEncoder, tokenUnitCount) {
        when (selectedEncoder) {
            is SeedPassEncoder -> {}
            is StringPassEncoder -> {
                tokenUnitCount?.let {
                    if (tokenUnitCount != 0 && passLength > it) passLength = it
                }
            }
        }
    }

    val approximateByteCount by remember(selectedEncoder, passLength) {
        derivedStateOf {
            when (selectedEncoder) {
                is SeedPassEncoder -> 0
                is StringPassEncoder -> approximateDecodedSize(passLength)
            }
        }
    }

    Column(
        modifier = Modifier.testTag("scroll_container")
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

        OutlinedTextField(
            value = masterKey,
            onValueChange = { masterKey = it },
            label = { Text("Master Key") },
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
            selected = selectedHasher,
            onSelected = { selectedHasher = it }
        )

        HorizontalDivider()

        Text(
            "Encoder Type",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        EncoderTypeSelector(
            encoders = encoders,
            selectedEncoder = selectedEncoder,
            onSelected = { selectedEncoder = it }
        )

        HorizontalDivider()

        if (selectedEncoder is StringPassEncoder)
            PassLengthSection(
                passLength = passLength,
                approximateByteCount = approximateByteCount,
                tokenUnitCount = tokenUnitCount!!,
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
                modifier = Modifier.testTag("submit_button"),
                enabled = name.isNotBlank() && masterKey.isNotBlank(),
                onClick = {
                    onSubmit(
                        KDFPassGenConfig(
                            id = 0, // ID will be handled by logic/DB
                            name = name.trim(),
                            passEncoder = selectedEncoder,
                            passwordLength = when (selectedEncoder) {
                                is SeedPassEncoder -> null
                                is StringPassEncoder -> passLength
                            },
                            preprocessConfig = PreprocessConfig(
                                trimLeadingAndTrailingSpaces = trimSpaces,
                                collapseMultipleSpaces = collapseSpaces,
                                convertToLowercase = lowercase
                            ),
                            inputHasher = selectedHasher
                        ),
                        masterKey.encodeToByteArray()
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
