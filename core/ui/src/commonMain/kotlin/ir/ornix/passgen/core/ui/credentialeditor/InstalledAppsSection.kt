package ir.ornix.passgen.core.ui.credentialeditor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.model.InstalledAppInfo
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.label_no_apps_found
import ir.ornix.passgen.core.ui.placeholder_search_apps
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun InstalledAppsSection(
    uiState: CredentialEditorUiState,
    dispatch: (CredentialEditorIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = uiState.appSearchQuery,
                    onValueChange = { dispatch(CredentialEditorIntent.AppSearchQueryChanged(it)) },
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.placeholder_search_apps),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
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
                        if (uiState.appSearchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                dispatch(
                                    CredentialEditorIntent.AppSearchQueryChanged(
                                        ""
                                    )
                                )
                            }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Filtered and Sorted App List
                val filteredAndSortedApps = remember(
                    uiState.installedApps,
                    uiState.appSearchQuery,
                    uiState.associatedPackageNames
                ) {
                    val query = uiState.appSearchQuery.trim().lowercase()
                    val filtered = if (query.isEmpty()) {
                        uiState.installedApps
                    } else {
                        uiState.installedApps.filter {
                            it.appName.lowercase().contains(query) || it.packageName.lowercase()
                                .contains(query)
                        }
                    }
                    filtered.sortedWith { app1, app2 ->
                        val sel1 = uiState.associatedPackageNames.contains(app1.packageName)
                        val sel2 = uiState.associatedPackageNames.contains(app2.packageName)
                        if (sel1 != sel2) {
                            sel2.compareTo(sel1)
                        } else {
                            app1.appName.lowercase().compareTo(app2.appName.lowercase())
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        uiState.isLoadingApps -> {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }

                        filteredAndSortedApps.isEmpty() -> {
                            Text(
                                text = stringResource(Res.string.label_no_apps_found),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        else -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                filteredAndSortedApps.forEach { app ->
                                    val isSelected =
                                        uiState.associatedPackageNames.contains(app.packageName)
                                    AppItemRow(
                                        app = app,
                                        isSelected = isSelected,
                                        onToggle = {
                                            dispatch(
                                                CredentialEditorIntent.TogglePackageSelection(
                                                    app.packageName
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
        }
    }
}


@Composable
private fun AppItemRow(
    app: InstalledAppInfo,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AppIcon(
                icon = app.icon,
                modifier = Modifier.size(36.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )
        }
    }
}
