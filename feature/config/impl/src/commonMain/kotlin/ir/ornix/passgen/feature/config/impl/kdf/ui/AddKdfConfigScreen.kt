package ir.ornix.passgen.feature.config.impl.kdf.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.content_desc_back
import ir.ornix.passgen.core.ui.create_config_title
import ir.ornix.passgen.core.ui.step_1_of_2_master_key
import ir.ornix.passgen.core.ui.step_2_of_2_details
import ir.ornix.passgen.feature.config.impl.kdf.presentation.AddKdfConfigIntent
import ir.ornix.passgen.feature.config.impl.kdf.presentation.AddKdfConfigStep
import ir.ornix.passgen.feature.config.impl.kdf.presentation.AddKdfConfigViewModel
import ir.ornix.passgen.feature.config.impl.kdf.ui.config_details_step.ConfigDetailsStepContent
import ir.ornix.passgen.feature.config.impl.kdf.ui.master_key_step.MasterKeyStepContent
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKdfConfigScreen(
    onNavigateBack: () -> Unit,
    onConfigCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AddKdfConfigViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var noticeAcknowledged by rememberSaveable { mutableStateOf(false) }

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

    Scaffold(
        modifier = modifier.fillMaxSize(),
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
                            validation = uiState.masterKeyValidation,
                            revealTimeoutMillis = uiState.visibilityTimeout.timeoutMillis,
                            onMasterKeyChange = {
                                viewModel.dispatch(AddKdfConfigIntent.MasterKeyChanged(it))
                            },
                            onConfirmMasterKeyChange = {
                                viewModel.dispatch(AddKdfConfigIntent.ConfirmMasterKeyChanged(it))
                            },
                            acknowledged = noticeAcknowledged,
                            onAcknowledgedChange = { noticeAcknowledged = it },
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
