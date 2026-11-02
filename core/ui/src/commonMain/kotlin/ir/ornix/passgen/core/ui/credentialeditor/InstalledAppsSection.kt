package ir.ornix.passgen.core.ui.credentialeditor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.domain.InstalledAppsProvider
import ir.ornix.passgen.core.model.InstalledAppInfo
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_add_custom_package
import ir.ornix.passgen.core.ui.label_custom_package_name
import ir.ornix.passgen.core.ui.label_no_apps_found
import ir.ornix.passgen.core.ui.label_select_installed_apps
import ir.ornix.passgen.core.ui.label_selected_apps_count
import ir.ornix.passgen.core.ui.placeholder_search_apps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun InstalledAppsSection(
    uiState: CredentialEditorUiState,
    dispatch: (CredentialEditorIntent) -> Unit,
    installedAppsProvider: InstalledAppsProvider,
    modifier: Modifier = Modifier
) {
    val isExpanded = uiState.isAppPickerExpanded

    LaunchedEffect(isExpanded) {
        if (isExpanded && uiState.installedApps.isEmpty() && !uiState.isLoadingApps) {
            dispatch(CredentialEditorIntent.LoadingAppsStarted)
            val apps = withContext(Dispatchers.Default) { installedAppsProvider.getInstalledApps() }
            dispatch(CredentialEditorIntent.AppsLoaded(apps))
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section Header Card
        OutlinedCard(
            onClick = { dispatch(CredentialEditorIntent.ToggleAppPickerExpanded(!isExpanded)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Apps,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )

                    Column {
                        Text(
                            text = stringResource(Res.string.label_select_installed_apps),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (uiState.associatedPackageNames.isNotEmpty()) {
                            Text(
                                text = stringResource(
                                    Res.string.label_selected_apps_count,
                                    uiState.associatedPackageNames.size
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Selected Package Chips
        AnimatedVisibility(visible = uiState.associatedPackageNames.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                uiState.associatedPackageNames.forEach { packageName ->
                    InputChip(
                        selected = true,
                        onClick = { },
                        label = { Text(packageName, style = MaterialTheme.typography.labelMedium) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        dispatch(
                                            CredentialEditorIntent.TogglePackageSelection(
                                                packageName
                                            )
                                        )
                                    }
                            )
                        }
                    )
                }
            }
        }

        // Expandable Installed Apps Picker Container
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
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

                    // Custom Package Name Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.customPackageInput,
                            onValueChange = {
                                dispatch(
                                    CredentialEditorIntent.CustomPackageInputChanged(
                                        it
                                    )
                                )
                            },
                            placeholder = {
                                Text(
                                    stringResource(Res.string.label_custom_package_name),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                dispatch(CredentialEditorIntent.AddCustomPackage(uiState.customPackageInput))
                            }),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            enabled = uiState.customPackageInput.isNotBlank(),
                            onClick = {
                                dispatch(CredentialEditorIntent.AddCustomPackage(uiState.customPackageInput))
                            },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(Res.string.action_add_custom_package),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

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
                            .heightIn(max = 220.dp),
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
