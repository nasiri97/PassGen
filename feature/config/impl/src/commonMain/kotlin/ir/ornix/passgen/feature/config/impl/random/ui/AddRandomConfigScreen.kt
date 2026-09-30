package ir.ornix.passgen.feature.config.impl.random.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.ui.component.EncoderTypeSelector
import ir.ornix.passgen.core.ui.component.NumberSlider
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.feature.config.impl.random.presentation.AddRandomConfigIntent
import ir.ornix.passgen.feature.config.impl.random.presentation.AddRandomConfigUiState
import ir.ornix.passgen.feature.config.impl.random.presentation.AddRandomConfigViewModel
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRandomConfigScreen(
    onNavigateBack: () -> Unit,
    onConfigCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AddRandomConfigViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onConfigCreated()
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
                        Text(
                            text = "Create Password Config",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                onNavigateBack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
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
            ConfigDetailsStepContent(
                uiState = uiState,
                encoders = uiState.availableEncoders,
                maxAvailablePassLength = uiState.maxAvailablePassLength,
                entropyByteSize = uiState.entropyByteSize,
                onNameChange = { viewModel.dispatch(AddRandomConfigIntent.NameChanged(it)) },
                onEncoderSelected = {
                    viewModel.dispatch(
                        AddRandomConfigIntent.EncoderSelected(
                            it
                        )
                    )
                },
                onPassLengthChange = {
                    viewModel.dispatch(
                        AddRandomConfigIntent.PassLengthChanged(
                            it
                        )
                    )
                },
                onBack = { onNavigateBack() },
                onCreate = { viewModel.dispatch(AddRandomConfigIntent.SubmitConfigClicked) }
            )
        }
    }
}

@Composable
private fun ConfigDetailsStepContent(
    uiState: AddRandomConfigUiState,
    encoders: List<PassEncoder>,
    maxAvailablePassLength: Int?,
    entropyByteSize: Int,
    onNameChange: (String) -> Unit,
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

        EncoderTypeSelector(
            encoders = encoders,
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
                text = "Password length: $passLength",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                text = "≈ $entropyByteSize bytes",
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