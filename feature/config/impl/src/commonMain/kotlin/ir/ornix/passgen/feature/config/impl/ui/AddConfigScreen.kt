package ir.ornix.passgen.feature.config.impl.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.ui.component.NumberSlider
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.feature.config.impl.presentation.AddConfigIntent
import ir.ornix.passgen.feature.config.impl.presentation.AddConfigStep
import ir.ornix.passgen.feature.config.impl.presentation.AddConfigUiState
import ir.ornix.passgen.feature.config.impl.presentation.AddConfigViewModel
import org.koin.compose.viewmodel.koinViewModel

data class MasterKeyValidation(
    val hasMinLength: Boolean,
    val hasLowercase: Boolean,
    val hasUppercase: Boolean,
    val hasDigit: Boolean,
    val hasSpecialChar: Boolean,
    val isRecommendedLength: Boolean,
    val keysMatch: Boolean
) {
    val isValid: Boolean
        get() = hasMinLength && hasLowercase && hasUppercase && hasDigit && hasSpecialChar && keysMatch
}

fun validateMasterKey(masterKey: String, confirmMasterKey: String): MasterKeyValidation {
    val hasMinLength = masterKey.length >= 24
    val hasLowercase = masterKey.any { it.isLowerCase() }
    val hasUppercase = masterKey.any { it.isUpperCase() }
    val hasDigit = masterKey.any { it.isDigit() }
    val hasSpecialChar = masterKey.any { !it.isLetterOrDigit() && !it.isWhitespace() }
    val isRecommendedLength = masterKey.length >= 64
    val keysMatch = masterKey.isNotEmpty() && masterKey == confirmMasterKey

    return MasterKeyValidation(
        hasMinLength = hasMinLength,
        hasLowercase = hasLowercase,
        hasUppercase = hasUppercase,
        hasDigit = hasDigit,
        hasSpecialChar = hasSpecialChar,
        isRecommendedLength = isRecommendedLength,
        keysMatch = keysMatch
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddConfigScreen(
    onNavigateBack: () -> Unit,
    onConfigCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AddConfigViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onConfigCreated()
        }
    }

    val masterKeyValidation by remember(uiState.masterKey, uiState.confirmMasterKey) {
        derivedStateOf { validateMasterKey(uiState.masterKey, uiState.confirmMasterKey) }
    }

    val encoders = remember(uiState.selectedHasher) {
        PassEncoder.getValidItems(uiState.selectedHasher)
    }

    val tokenUnitCount by remember(uiState.selectedHasher, uiState.selectedEncoder) {
        derivedStateOf {
            when (val encoder = uiState.selectedEncoder) {
                is SeedPassEncoder -> null
                is StringPassEncoder -> encoder.getTokenLength(byteSize = uiState.selectedHasher.outputByteSize)
            }
        }
    }

    val approximateByteCount by remember(uiState.selectedEncoder, uiState.passLength) {
        derivedStateOf {
            when (val encoder = uiState.selectedEncoder) {
                is SeedPassEncoder -> 0
                is StringPassEncoder -> encoder.getEntropyByteSize(uiState.passLength)
            }
        }
    }

    Scaffold(
        modifier = modifier
            .secureContent()
            .fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Create Password Config",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = when (uiState.step) {
                                    AddConfigStep.MasterKey -> "Step 1 of 2: Master Key"
                                    AddConfigStep.ConfigDetails -> "Step 2 of 2: Configuration Details"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (uiState.step == AddConfigStep.ConfigDetails) {
                                    viewModel.dispatch(AddConfigIntent.PreviousStepClicked)
                                } else {
                                    onNavigateBack()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
                LinearProgressIndicator(
                    progress = {
                        when (uiState.step) {
                            AddConfigStep.MasterKey -> 0.5f
                            AddConfigStep.ConfigDetails -> 1.0f
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .testTag("scroll_container")
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedContent(
                targetState = uiState.step,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { targetStep ->
                when (targetStep) {
                    AddConfigStep.MasterKey -> {
                        MasterKeyStepContent(
                            uiState = uiState,
                            validation = masterKeyValidation,
                            onMasterKeyChange = { viewModel.dispatch(AddConfigIntent.MasterKeyChanged(it)) },
                            onConfirmMasterKeyChange = { viewModel.dispatch(AddConfigIntent.ConfirmMasterKeyChanged(it)) },
                            onNext = { viewModel.dispatch(AddConfigIntent.NextStepClicked) },
                            onCancel = onNavigateBack
                        )
                    }

                    AddConfigStep.ConfigDetails -> {
                        ConfigDetailsStepContent(
                            uiState = uiState,
                            encoders = encoders,
                            tokenUnitCount = tokenUnitCount,
                            approximateByteCount = approximateByteCount,
                            onNameChange = { viewModel.dispatch(AddConfigIntent.NameChanged(it)) },
                            onTrimSpacesChange = { viewModel.dispatch(AddConfigIntent.TrimSpacesToggled(it)) },
                            onCollapseSpacesChange = { viewModel.dispatch(AddConfigIntent.CollapseSpacesToggled(it)) },
                            onLowercaseChange = { viewModel.dispatch(AddConfigIntent.LowercaseToggled(it)) },
                            onHasherSelected = { viewModel.dispatch(AddConfigIntent.HasherSelected(it)) },
                            onEncoderSelected = { viewModel.dispatch(AddConfigIntent.EncoderSelected(it)) },
                            onPassLengthChange = { viewModel.dispatch(AddConfigIntent.PassLengthChanged(it)) },
                            onBack = { viewModel.dispatch(AddConfigIntent.PreviousStepClicked) },
                            onCreate = { viewModel.dispatch(AddConfigIntent.SubmitConfigClicked) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MasterKeyStepContent(
    uiState: AddConfigUiState,
    validation: MasterKeyValidation,
    onMasterKeyChange: (String) -> Unit,
    onConfirmMasterKeyChange: (String) -> Unit,
    onNext: () -> Unit,
    onCancel: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Set Up Master Key",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Enter and confirm your Master Key. It acts as a secret seed to generate your deterministic passwords.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = uiState.masterKey,
            onValueChange = onMasterKeyChange,
            label = { Text("Master Key") },
            minLines = 5,
            maxLines = 5,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("master_key_input")
        )

        OutlinedTextField(
            value = uiState.confirmMasterKey,
            onValueChange = onConfirmMasterKeyChange,
            label = { Text("Confirm Master Key") },
            minLines = 5,
            maxLines = 5,
            isError = uiState.confirmMasterKey.isNotEmpty() && !validation.keysMatch,
            supportingText = if (uiState.confirmMasterKey.isNotEmpty() && !validation.keysMatch) {
                { Text("Master keys do not match", color = MaterialTheme.colorScheme.error) }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("confirm_master_key_input")
        )

        MasterKeyValidationCard(
            validation = validation,
            masterKeyLength = uiState.masterKey.length
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }

            Spacer(Modifier.width(8.dp))

            Button(
                modifier = Modifier.testTag("next_button"),
                enabled = validation.isValid,
                onClick = onNext
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
private fun MasterKeyValidationCard(
    validation: MasterKeyValidation,
    masterKeyLength: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Master Key Requirements",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            ValidationRequirementItem(
                label = "At least 24 characters (Current: $masterKeyLength)",
                isSatisfied = validation.hasMinLength,
                isRecommended = validation.isRecommendedLength,
                recommendationLabel = if (validation.isRecommendedLength) " (Recommended 64 reached)" else " (Recommended: 64)"
            )
            ValidationRequirementItem(
                label = "At least one lowercase letter (a-z)",
                isSatisfied = validation.hasLowercase
            )
            ValidationRequirementItem(
                label = "At least one uppercase letter (A-Z)",
                isSatisfied = validation.hasUppercase
            )
            ValidationRequirementItem(
                label = "At least one number (0-9)",
                isSatisfied = validation.hasDigit
            )
            ValidationRequirementItem(
                label = "At least one special character (!@#$...)",
                isSatisfied = validation.hasSpecialChar
            )
            ValidationRequirementItem(
                label = "Master keys match",
                isSatisfied = validation.keysMatch
            )
        }
    }
}

@Composable
private fun ValidationRequirementItem(
    label: String,
    isSatisfied: Boolean,
    isRecommended: Boolean = false,
    recommendationLabel: String = ""
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (isSatisfied) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isSatisfied) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            },
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label + recommendationLabel,
            style = MaterialTheme.typography.bodySmall,
            color = when {
                isRecommended -> MaterialTheme.colorScheme.primary
                isSatisfied -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun ConfigDetailsStepContent(
    uiState: AddConfigUiState,
    encoders: List<PassEncoder>,
    tokenUnitCount: Int?,
    approximateByteCount: Int,
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
            text = "Configuration Details",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = { Text("Configuration Name") },
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
            encoders = encoders,
            selectedEncoder = uiState.selectedEncoder,
            onSelected = onEncoderSelected
        )

        HorizontalDivider()

        if (uiState.selectedEncoder is StringPassEncoder && tokenUnitCount != null) {
            PassLengthSection(
                passLength = uiState.passLength,
                approximateByteCount = approximateByteCount,
                tokenUnitCount = tokenUnitCount,
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
                Text("Back")
            }

            Spacer(Modifier.width(8.dp))

            Button(
                modifier = Modifier.testTag("submit_button"),
                enabled = uiState.name.isNotBlank() && !uiState.isSubmitting,
                onClick = onCreate
            ) {
                Text(if (uiState.isSubmitting) "Creating..." else "Create")
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
            onCheckedChange = trimSpacesChanged
        )

        LabeledCheckbox(
            label = "Collapse multiple spaces",
            checked = collapseSpaces,
            onCheckedChange = collapseSpacesChanged
        )

        LabeledCheckbox(
            label = "Convert to lowercase",
            checked = lowercase,
            onCheckedChange = lowercaseChanged
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
