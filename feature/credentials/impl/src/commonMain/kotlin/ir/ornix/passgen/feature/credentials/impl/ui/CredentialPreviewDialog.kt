package ir.ornix.passgen.feature.credentials.impl.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_cancel
import ir.ornix.passgen.core.ui.action_close
import ir.ornix.passgen.core.ui.action_edit
import ir.ornix.passgen.core.ui.credential_details
import ir.ornix.passgen.core.ui.label_email
import ir.ornix.passgen.core.ui.label_keywords
import ir.ornix.passgen.core.ui.label_package_names
import ir.ornix.passgen.core.ui.label_phone
import ir.ornix.passgen.core.ui.label_username
import ir.ornix.passgen.core.ui.label_web_domains
import ir.ornix.passgen.core.ui.section_account_details
import ir.ornix.passgen.core.ui.section_apps_and_webs_and_keywords
import ir.ornix.passgen.core.ui.section_description
import ir.ornix.passgen.core.ui.security.PasswordDisplay
import org.jetbrains.compose.resources.stringResource

private const val DEFAULT_FOR_TITLELESS = "-"

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CredentialPreviewDialog(
    credential: Credential,
    onDismissRequest: () -> Unit,
    onEditRequested: () -> Unit,
    onCopyPasswordRequested: (Password) -> Unit,
    onCopyTextRequested: (String) -> Unit,
    revealTimeoutMillis: Long,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
    onSnackbarAvailabilityChanged: (Boolean) -> Unit = {}
) {

    val isSnackbarAvailableInChildren = true

    LaunchedEffect(isSnackbarAvailableInChildren) {
        onSnackbarAvailabilityChanged(isSnackbarAvailableInChildren)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = credential.title?.take(1)?.uppercase()
                                            ?: DEFAULT_FOR_TITLELESS,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = credential.title ?: DEFAULT_FOR_TITLELESS,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = stringResource(Res.string.credential_details),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(Res.string.action_close)
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )

                    // Scrollable Details Body
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .heightIn(max = 520.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        // Password Section
                        PasswordDisplay(
                            modifier = Modifier.fillMaxWidth(),
                            password = credential.password,
                            revealTimeoutMillis = revealTimeoutMillis,
                            isClickable = true,
                            onClick = { onCopyPasswordRequested(credential.password) }
                        )

                        // Identity & Contact
                        if (!credential.username.isNullOrBlank() || !credential.email.isNullOrBlank() || !credential.phone.isNullOrBlank()) {
                            DetailSectionContainer(title = stringResource(Res.string.section_account_details)) {
                                credential.username?.takeIf { it.isNotBlank() }?.let { username ->
                                    DetailRowItem(
                                        label = stringResource(Res.string.label_username),
                                        value = username,
                                        icon = Icons.Outlined.Person,
                                        onCopy = { onCopyTextRequested(username) }
                                    )
                                }

                                credential.email?.takeIf { it.isNotBlank() }?.let { email ->
                                    DetailRowItem(
                                        label = stringResource(Res.string.label_email),
                                        value = email,
                                        icon = Icons.Outlined.Email,
                                        onCopy = { onCopyTextRequested(email) }
                                    )
                                }

                                credential.phone?.takeIf { it.isNotBlank() }?.let { phone ->
                                    DetailRowItem(
                                        label = stringResource(Res.string.label_phone),
                                        value = phone,
                                        icon = Icons.Outlined.Phone,
                                        onCopy = { onCopyTextRequested(phone) }
                                    )
                                }
                            }
                        }

                        // Apps & Web Linking & Keywords
                        if (credential.associatedPackageNames.isNotEmpty() || credential.associatedWebDomains.isNotEmpty()) {
                            DetailSectionContainer(title = stringResource(Res.string.section_apps_and_webs_and_keywords)) {
                                AssociationSection(
                                    label = stringResource(Res.string.label_package_names),
                                    items = credential.associatedPackageNames,
                                    onCopyTextRequested = onCopyTextRequested
                                )

                                AssociationSection(
                                    label = stringResource(Res.string.label_web_domains),
                                    items = credential.associatedWebDomains,
                                    onCopyTextRequested = onCopyTextRequested
                                )

                                AssociationSection(
                                    label = stringResource(Res.string.label_keywords),
                                    items = credential.associatedKeywords,
                                    onCopyTextRequested = onCopyTextRequested
                                )
                            }
                        }

                        // Description / Notes
                        credential.description?.takeIf { it.isNotBlank() }?.let { desc ->
                            DetailSectionContainer(title = stringResource(Res.string.section_description)) {
                                OutlinedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                ) {
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(14.dp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )

                    // Actions Footer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text(stringResource(Res.string.action_cancel))
                        }

                        Spacer(Modifier.width(12.dp))

                        Button(
                            onClick = onEditRequested,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(stringResource(Res.string.action_edit))
                        }
                    }
                }

                if (snackbarHostState != null) {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AssociationSection(
    label: String,
    items: Set<String>,
    onCopyTextRequested: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isNotEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items.forEach { item ->
                    AssistChip(
                        onClick = { onCopyTextRequested(item) },
                        label = {
                            Text(
                                item,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun DetailSectionContainer(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

@Composable
private fun DetailRowItem(
    label: String,
    value: String,
    icon: ImageVector,
    onCopy: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCopy),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )

                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
