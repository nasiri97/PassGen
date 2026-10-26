package ir.ornix.passgen.feature.credentials.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_add
import ir.ornix.passgen.core.ui.action_clear
import ir.ornix.passgen.core.ui.component.ConfirmDeleteDialog
import ir.ornix.passgen.core.ui.copy_disabled_toast_message
import ir.ornix.passgen.core.ui.copy_success_toast_message
import ir.ornix.passgen.core.ui.credentialeditor.CredentialEditor
import ir.ornix.passgen.core.ui.no_credentials_found_message
import ir.ornix.passgen.core.ui.no_saved_credentials_message
import ir.ornix.passgen.core.ui.no_saved_credentials_subtitle
import ir.ornix.passgen.core.ui.search_credentials_placeholder
import ir.ornix.passgen.feature.credentials.impl.presentation.CredentialsIntent
import ir.ornix.passgen.feature.credentials.impl.presentation.CredentialsUiState
import ir.ornix.passgen.feature.credentials.impl.presentation.CredentialsViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CredentialsScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: CredentialsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val copyDisabledMessage = stringResource(Res.string.copy_disabled_toast_message)
    val copySuccessMessage = stringResource(Res.string.copy_success_toast_message)

    val onCopyRequested: (Password) -> Unit = { password ->
        if (uiState.isCopyEnabled) {
            clipboardManager.setText(AnnotatedString(password.value.concatToString()))
            scope.launch {
                snackbarHostState.showSnackbar(copySuccessMessage)
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(copyDisabledMessage)
            }
        }
    }

    val onCopyTextRequested: (String) -> Unit = { text ->
        clipboardManager.setText(AnnotatedString(text))
        scope.launch {
            snackbarHostState.showSnackbar(copySuccessMessage)
        }
    }

    var isSnackbarAvailableInNewCredentialDialog by remember { mutableStateOf(false) }
    var isSnackbarAvailableInEditCredentialDialog by remember { mutableStateOf(false) }
    var isSnackbarAvailableInCredentialPreviewDialog by remember { mutableStateOf(false) }

    val isSnackbarAvailableInChildren =
        (uiState.isAddingCredential && isSnackbarAvailableInNewCredentialDialog) ||
                (uiState.selectedCredentialForDetail != null && isSnackbarAvailableInCredentialPreviewDialog) ||
                (uiState.selectedCredentialForEdit != null && isSnackbarAvailableInEditCredentialDialog)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Credential Screen content
        CredentialsScreenContent(
            uiState = uiState,
            viewModel = viewModel,
            snackbarHost = {
                if (!isSnackbarAvailableInChildren) {
                    SnackbarHost(snackbarHostState)
                }
            }
        )

        // Detail Modal Dialog
        if (uiState.selectedCredentialForDetail != null) {
            val credential = uiState.selectedCredentialForDetail!!
            CredentialPreviewDialog(
                credential = credential,
                onDismissRequest = {
                    viewModel.dispatch(CredentialsIntent.SelectCredentialForDetail(null))
                },
                onEditRequested = {
                    viewModel.dispatch(CredentialsIntent.SelectCredentialForDetail(null))
                    viewModel.dispatch(CredentialsIntent.SelectCredentialForEdit(credential))
                },
                onCopyPasswordRequested = onCopyRequested,
                onCopyTextRequested = onCopyTextRequested,
                revealTimeoutMillis = uiState.visibilityTimeout.timeoutMillis,
                snackbarHostState = snackbarHostState,
                onSnackbarAvailabilityChanged = {
                    isSnackbarAvailableInCredentialPreviewDialog = it
                }
            )
        }

        // Direct Add Modal Dialog
        if (uiState.isAddingCredential) {
            CredentialEditor(
                password = null,
                isPasswordEditable = true,
                onSubmit = { newCredential ->
                    viewModel.dispatch(CredentialsIntent.CreateCredential(newCredential))
                },
                onDismissRequest = {
                    viewModel.dispatch(CredentialsIntent.DismissAddCredentialDialog)
                },
                onCopyRequested = onCopyRequested,
                revealTimeoutMillis = uiState.visibilityTimeout.timeoutMillis,
                snackbarHostState = snackbarHostState,
                onSnackbarAvailabilityChanged = { isSnackbarAvailableInNewCredentialDialog = it }
            )
        }

        // Edit Modal Dialog
        if (uiState.selectedCredentialForEdit != null) {
            val credentialToEdit = uiState.selectedCredentialForEdit!!
            CredentialEditor(
                password = credentialToEdit.password,
                existingCredential = credentialToEdit,
                onSubmit = { updated ->
                    viewModel.dispatch(CredentialsIntent.UpdateCredential(updated))
                },
                onDismissRequest = {
                    viewModel.dispatch(CredentialsIntent.SelectCredentialForEdit(null))
                },
                onCopyRequested = onCopyRequested,
                revealTimeoutMillis = uiState.visibilityTimeout.timeoutMillis,
                snackbarHostState = snackbarHostState,
                onSnackbarAvailabilityChanged = { isSnackbarAvailableInEditCredentialDialog = it }
            )
        }

        // Confirm Delete Dialog
        if (uiState.selectedCredentialForDelete != null) {
            val credentialToDelete = uiState.selectedCredentialForDelete!!
            ConfirmDeleteDialog(
                itemLabel = credentialToDelete.title,
                onConfirmDelete = {
                    viewModel.dispatch(CredentialsIntent.DeleteCredential(credentialToDelete.id))
                },
                onDismissRequest = {
                    viewModel.dispatch(CredentialsIntent.SelectCredentialForDelete(null))
                }
            )
        }
    }
}


@Composable
private fun CredentialsScreenContent(
    uiState: CredentialsUiState,
    viewModel: CredentialsViewModel,
    modifier: Modifier = Modifier,
    snackbarHost: @Composable () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = snackbarHost,
        floatingActionButton = {
            if (uiState.credentials.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.dispatch(CredentialsIntent.OpenAddCredentialDialog) },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text(stringResource(Res.string.action_add)) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(16.dp),
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar (Only shown when there are saved credentials)
            if (uiState.credentials.isNotEmpty()) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.dispatch(CredentialsIntent.SearchQueryChanged(it)) },
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.search_credentials_placeholder),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.dispatch(
                                    CredentialsIntent.SearchQueryChanged(
                                        ""
                                    )
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(Res.string.action_clear),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // Content Body
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.credentials.isEmpty()) {
                EmptyStateCard(
                    title = stringResource(Res.string.no_saved_credentials_message),
                    subtitle = stringResource(Res.string.no_saved_credentials_subtitle),
                    onAddClicked = { viewModel.dispatch(CredentialsIntent.OpenAddCredentialDialog) }
                )
            } else if (uiState.filteredCredentials.isEmpty()) {
                NoResultsCard(
                    onClearSearch = { viewModel.dispatch(CredentialsIntent.SearchQueryChanged("")) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(
                        items = uiState.filteredCredentials,
                        key = { it.id }
                    ) { credential ->
                        CredentialItemCard(
                            credential = credential,
                            onClick = {
                                viewModel.dispatch(
                                    CredentialsIntent.SelectCredentialForDetail(
                                        credential
                                    )
                                )
                            },
                            onEdit = {
                                viewModel.dispatch(
                                    CredentialsIntent.SelectCredentialForEdit(
                                        credential
                                    )
                                )
                            },
                            onDelete = {
                                viewModel.dispatch(
                                    CredentialsIntent.SelectCredentialForDelete(
                                        credential
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun EmptyStateCard(
    title: String,
    subtitle: String,
    onAddClicked: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onAddClicked,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.action_add))
            }
        }
    }
}

@Composable
private fun NoResultsCard(onClearSearch: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(Res.string.no_credentials_found_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
            onClick = onClearSearch,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(Res.string.action_clear))
        }
    }
}
