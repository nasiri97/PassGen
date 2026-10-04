package ir.ornix.passgen.feature.config.impl.kdf.ui

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_back
import ir.ornix.passgen.core.ui.action_cancel
import ir.ornix.passgen.core.ui.action_create
import ir.ornix.passgen.core.ui.action_creating
import ir.ornix.passgen.core.ui.action_next
import ir.ornix.passgen.core.ui.component.EncoderTypeSelector
import ir.ornix.passgen.core.ui.component.HashingTypeSelector
import ir.ornix.passgen.core.ui.component.NumberSlider
import ir.ornix.passgen.core.ui.content_desc_back
import ir.ornix.passgen.core.ui.create_config_title
import ir.ornix.passgen.core.ui.error_master_keys_mismatch
import ir.ornix.passgen.core.ui.label_approx_bytes_format
import ir.ornix.passgen.core.ui.label_config_name
import ir.ornix.passgen.core.ui.label_confirm_master_key
import ir.ornix.passgen.core.ui.label_master_key
import ir.ornix.passgen.core.ui.label_password_length_format
import ir.ornix.passgen.core.ui.master_key_explanation
import ir.ornix.passgen.core.ui.master_key_requirements_title
import ir.ornix.passgen.core.ui.option_collapse_spaces
import ir.ornix.passgen.core.ui.option_convert_lowercase
import ir.ornix.passgen.core.ui.option_trim_spaces
import ir.ornix.passgen.core.ui.req_digit
import ir.ornix.passgen.core.ui.req_lowercase
import ir.ornix.passgen.core.ui.req_match
import ir.ornix.passgen.core.ui.req_min_length
import ir.ornix.passgen.core.ui.req_rec_reached
import ir.ornix.passgen.core.ui.req_rec_target
import ir.ornix.passgen.core.ui.req_special
import ir.ornix.passgen.core.ui.req_uppercase
import ir.ornix.passgen.core.ui.security.SecurePasswordField
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.core.ui.setup_master_key_title
import ir.ornix.passgen.core.ui.step_1_of_2_master_key
import ir.ornix.passgen.core.ui.step_2_of_2_details
import ir.ornix.passgen.core.ui.title_config_details
import ir.ornix.passgen.core.ui.title_preprocessing
import ir.ornix.passgen.feature.config.impl.kdf.presentation.AddKdfConfigIntent
import ir.ornix.passgen.feature.config.impl.kdf.presentation.AddKdfConfigStep
import ir.ornix.passgen.feature.config.impl.kdf.presentation.AddKdfConfigViewModel
import ir.ornix.passgen.feature.config.impl.kdf.presentation.AddKdsConfigUiState
import org.jetbrains.compose.resources.stringResource
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

fun validateMasterKey(masterKey: ByteArray, confirmMasterKey: ByteArray): MasterKeyValidation {
    val mkStr = masterKey.decodeToString()

    val hasMinLength = masterKey.size >= 24
    val hasLowercase = mkStr.any { it.isLowerCase() }
    val hasUppercase = mkStr.any { it.isUpperCase() }
    val hasDigit = mkStr.any { it.isDigit() }
    val hasSpecialChar = mkStr.any { !it.isLetterOrDigit() && !it.isWhitespace() }
    val isRecommendedLength = masterKey.size >= 64
    val keysMatch = masterKey.isNotEmpty() && masterKey.contentEquals(confirmMasterKey)

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
fun AddKdfConfigScreen(
    onNavigateBack: () -> Unit,
    onConfigCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AddKdfConfigViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var masterKeyUpdate by rememberSaveable {
        mutableStateOf(Long.MIN_VALUE)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onConfigCreated()
        }
    }

    LaunchedEffect(uiState.isProcessCancelled) {
        if (uiState.isProcessCancelled) {
            onNavigateBack()
        }
    }

    val masterKeyValidation by remember(masterKeyUpdate) {
        derivedStateOf { validateMasterKey(viewModel.masterKey, viewModel.confirmMasterKey) }
    }

    Scaffold(
        modifier = modifier.secureContent().fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = stringResource(Res.string.create_config_title),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = when (uiState.step) {
                                    AddKdfConfigStep.MasterKey -> stringResource(Res.string.step_1_of_2_master_key)
                                    AddKdfConfigStep.ConfigDetails -> stringResource(Res.string.step_2_of_2_details)
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (uiState.step == AddKdfConfigStep.ConfigDetails) {
                                    viewModel.dispatch(AddKdfConfigIntent.PreviousStepClicked)
                                } else {
                                    viewModel.dispatch(AddKdfConfigIntent.ProcessCancelled)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.content_desc_back)
                            )
                        }
                    }
                )
                LinearProgressIndicator(
                    progress = {
                        when (uiState.step) {
                            AddKdfConfigStep.MasterKey -> 0.5f
                            AddKdfConfigStep.ConfigDetails -> 1.0f
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
                    AddKdfConfigStep.MasterKey -> {
                        MasterKeyStepContent(
                            uiState = uiState,
                            validation = masterKeyValidation,
                            masterKey = viewModel.masterKey,
                            confirmMasterKey = viewModel.confirmMasterKey,
                            onMasterKeyChange = {
                                viewModel.updateMasterKey(it)
                                masterKeyUpdate++
                            },
                            onConfirmMasterKeyChange = {
                                viewModel.updateConfirmMasterKey(it)
                                masterKeyUpdate++
                            },
                            onNext = { viewModel.dispatch(AddKdfConfigIntent.NextStepClicked) },
                            onCancel = { viewModel.dispatch(AddKdfConfigIntent.ProcessCancelled) }
                        )
                    }

                    AddKdfConfigStep.ConfigDetails -> {
                        ConfigDetailsStepContent(
                            uiState = uiState,
                            availableEncoders = uiState.availableEncoders,
                            maxAvailablePassLength = uiState.maxAvailablePassLength,
                            entropyByteSize = uiState.entropyByteSize,
                            onNameChange = { viewModel.dispatch(AddKdfConfigIntent.NameChanged(it)) },
                            onTrimSpacesChange = {
                                viewModel.dispatch(
                                    AddKdfConfigIntent.TrimSpacesToggled(
                                        it
                                    )
                                )
                            },
                            onCollapseSpacesChange = {
                                viewModel.dispatch(
                                    AddKdfConfigIntent.CollapseSpacesToggled(
                                        it
                                    )
                                )
                            },
                            onLowercaseChange = {
                                viewModel.dispatch(
                                    AddKdfConfigIntent.LowercaseToggled(
                                        it
                                    )
                                )
                            },
                            onHasherSelected = {
                                viewModel.dispatch(
                                    AddKdfConfigIntent.HasherSelected(
                                        it
                                    )
                                )
                            },
                            onEncoderSelected = {
                                viewModel.dispatch(
                                    AddKdfConfigIntent.EncoderSelected(
                                        it
                                    )
                                )
                            },
                            onPassLengthChange = {
                                viewModel.dispatch(
                                    AddKdfConfigIntent.PassLengthChanged(
                                        it
                                    )
                                )
                            },
                            onBack = { viewModel.dispatch(AddKdfConfigIntent.PreviousStepClicked) },
                            onCreate = { viewModel.dispatch(AddKdfConfigIntent.SubmitConfigClicked) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MasterKeyStepContent(
    uiState: AddKdsConfigUiState,
    validation: MasterKeyValidation,
    masterKey: ByteArray,
    confirmMasterKey: ByteArray,
    onMasterKeyChange: (ByteArray) -> Unit,
    onConfirmMasterKeyChange: (ByteArray) -> Unit,
    onNext: () -> Unit,
    onCancel: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(Res.string.setup_master_key_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(Res.string.master_key_explanation),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SecurePasswordField(
            value = masterKey,
            onValueChange = onMasterKeyChange,
            label = { Text(stringResource(Res.string.label_master_key)) },
            modifier = Modifier.fillMaxWidth(),
            testTag = "master_key_input"
        )

        SecurePasswordField(
            value = confirmMasterKey,
            onValueChange = onConfirmMasterKeyChange,
            label = { Text(stringResource(Res.string.label_confirm_master_key)) },
            isError = confirmMasterKey.isNotEmpty() && !validation.keysMatch,
            supportingText = if (confirmMasterKey.isNotEmpty() && !validation.keysMatch) {
                {
                    Text(
                        stringResource(Res.string.error_master_keys_mismatch),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            testTag = "confirm_master_key_input"
        )

        MasterKeyValidationCard(
            validation = validation,
            masterKeyLength = masterKey.size
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(Res.string.action_cancel))
            }

            Spacer(Modifier.width(8.dp))

            Button(
                modifier = Modifier.testTag("next_button"),
                enabled = validation.isValid,
                onClick = onNext
            ) {
                Text(stringResource(Res.string.action_next))
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
                text = stringResource(Res.string.master_key_requirements_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            ValidationRequirementItem(
                label = stringResource(Res.string.req_min_length, masterKeyLength),
                isSatisfied = validation.hasMinLength,
                isRecommended = validation.isRecommendedLength,
                recommendationLabel = if (validation.isRecommendedLength) stringResource(Res.string.req_rec_reached) else stringResource(
                    Res.string.req_rec_target
                )
            )
            ValidationRequirementItem(
                label = stringResource(Res.string.req_lowercase),
                isSatisfied = validation.hasLowercase
            )
            ValidationRequirementItem(
                label = stringResource(Res.string.req_uppercase),
                isSatisfied = validation.hasUppercase
            )
            ValidationRequirementItem(
                label = stringResource(Res.string.req_digit),
                isSatisfied = validation.hasDigit
            )
            ValidationRequirementItem(
                label = stringResource(Res.string.req_special),
                isSatisfied = validation.hasSpecialChar
            )
            ValidationRequirementItem(
                label = stringResource(Res.string.req_match),
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
fun PassLengthSection(
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
