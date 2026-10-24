package ir.ornix.passgen.feature.credentials.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_clear
import ir.ornix.passgen.core.ui.component.ConfirmDeleteDialog
import ir.ornix.passgen.core.ui.copy_disabled_toast_message
import ir.ornix.passgen.core.ui.copy_success_toast_message
import ir.ornix.passgen.core.ui.credentialeditor.CredentialEditor
import ir.ornix.passgen.core.ui.no_credentials_found_message
import ir.ornix.passgen.core.ui.no_saved_credentials_message
import ir.ornix.passgen.core.ui.no_saved_credentials_subtitle
import ir.ornix.passgen.core.ui.saved_credentials_count
import ir.ornix.passgen.core.ui.search_credentials_placeholder
import ir.ornix.passgen.core.ui.title_saved_passwords
import ir.ornix.passgen.feature.credentials.impl.presentation.CredentialsIntent
import ir.ornix.passgen.feature.credentials.impl.presentation.CredentialsViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


private const val DEFAULT_FOR_TITLELESS = "-"

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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Title & Search Header
            HeaderSection(
                searchQuery = uiState.searchQuery,
                credentialCount = uiState.filteredCredentials.size,
                onSearchQueryChanged = { viewModel.dispatch(CredentialsIntent.SearchQueryChanged(it)) }
            )

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
                    subtitle = stringResource(Res.string.no_saved_credentials_subtitle)
                )
            } else if (uiState.filteredCredentials.isEmpty()) {
                NoResultsCard(
                    onClearSearch = { viewModel.dispatch(CredentialsIntent.SearchQueryChanged("")) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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

    // --- Dialogs ---

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
            revealTimeoutMillis = uiState.revealTimeoutMillis
        )
    }

    // Edit Modal Dialog (Reusing NewAccountDialog in Edit mode)
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
            revealTimeoutMillis = uiState.revealTimeoutMillis
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

@Composable
private fun HeaderSection(
    searchQuery: String,
    credentialCount: Int,
    onSearchQueryChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.title_saved_passwords),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            ) {
                Text(
                    text = stringResource(Res.string.saved_credentials_count, credentialCount),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text(stringResource(Res.string.search_credentials_placeholder)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(Res.string.action_clear)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
    }
}


@Composable
private fun EmptyStateCard(title: String, subtitle: String) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

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

        Button(onClick = onClearSearch) {
            Text(stringResource(Res.string.action_clear))
        }
    }
}