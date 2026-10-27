package ir.ornix.passgen.core.ui.passgen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.PassGenItem
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import ir.ornix.passgen.core.ui.component.ConfirmDeleteDialog
import ir.ornix.passgen.core.ui.credentialeditor.CredentialEditor
import ir.ornix.passgen.core.ui.security.PasswordDisplay
import kotlinx.coroutines.launch

private val passCardCornerRadius = 20.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassGenItemCard(
    passGenItem: PassGenItem,
    onRemove: () -> Unit,
    addNewCredential: (credential: Credential) -> Unit,
    showPassGenInfoDialog: (PassGenConfig) -> Unit,
    onCopyRequested: (Password) -> Unit,
    revealTimeoutMillis: Long,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
    onSnackbarAvailabilityChanged: (Boolean) -> Unit = {}
) {
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var showCredentialDialog by remember { mutableStateOf(false) }

    var isSnackbarAvailableInCredentialDialog by remember { mutableStateOf(false) }

    val isSnackbarAvailableInChildren =
        showCredentialDialog && isSnackbarAvailableInCredentialDialog

    LaunchedEffect(isSnackbarAvailableInChildren) {
        onSnackbarAvailabilityChanged(isSnackbarAvailableInChildren)
    }

    val password = passGenItem.password

    if (showConfirmDeleteDialog) {
        ConfirmDeleteDialog(
            itemLabel = passGenItem.config.name,
            onConfirmDelete = {
                onRemove()
                showConfirmDeleteDialog = false
            },
            onDismissRequest = { showConfirmDeleteDialog = false }
        )
    }

    if (showCredentialDialog && password != null && password.value.isNotEmpty()) {
        CredentialEditor(
            password = password,
            defaultTitle = passGenItem.config.name,
            onSubmit = { newCredential ->
                addNewCredential(newCredential)
                showCredentialDialog = false
            },
            onDismissRequest = { showCredentialDialog = false },
            onCopyRequested = onCopyRequested,
            revealTimeoutMillis = revealTimeoutMillis,
            snackbarHostState = snackbarHostState,
            onSnackbarAvailabilityChanged = { isSnackbarAvailableInCredentialDialog = it }
        )
    }

    val swipeState = rememberSwipeToDismissBoxState()
    val scope = rememberCoroutineScope()

    SwipeToDismissBox(
        modifier = modifier.fillMaxWidth().testTag("pass_gen_item_card"),
        state = swipeState,
        onDismiss = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    showCredentialDialog = true
                    scope.launch { swipeState.reset() }
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    showConfirmDeleteDialog = true
                    scope.launch { swipeState.reset() }
                }

                SwipeToDismissBoxValue.Settled -> {}
            }
        },
        backgroundContent = {
            val direction = swipeState.dismissDirection
            if (direction != SwipeToDismissBoxValue.Settled) {
                val color = if (direction == SwipeToDismissBoxValue.StartToEnd) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer
                }
                val iconColor = if (direction == SwipeToDismissBoxValue.StartToEnd) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                }
                val icon = if (direction == SwipeToDismissBoxValue.StartToEnd) {
                    Icons.Default.Update
                } else {
                    Icons.Default.Delete
                }
                val alignment = if (direction == SwipeToDismissBoxValue.StartToEnd) {
                    Alignment.CenterStart
                } else {
                    Alignment.CenterEnd
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color, shape = RoundedCornerShape(passCardCornerRadius))
                        .padding(horizontal = 24.dp),
                    contentAlignment = alignment
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor)
                }
            }
        }
    ) {
        Content(
            passGenItem = passGenItem,
            showPassGenInfoDialog = { showPassGenInfoDialog(passGenItem.config) },
            onCopyRequested = onCopyRequested,
            revealTimeoutMillis = revealTimeoutMillis
        )
    }
}

@Composable
private fun Content(
    passGenItem: PassGenItem,
    showPassGenInfoDialog: () -> Unit,
    onCopyRequested: (Password) -> Unit,
    revealTimeoutMillis: Long,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(passCardCornerRadius)),
        shape = RoundedCornerShape(passCardCornerRadius),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        modifier = Modifier.weight(1F),
                        text = passGenItem.config.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    TypeBadge(
                        typeBrief = passGenItem.config.typeBrief,
                        onClick = showPassGenInfoDialog
                    )
                }
            }

            // Divider between Header and Password Display
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )

            // Password Display
            PasswordDisplay(
                modifier = Modifier.fillMaxWidth(),
                password = passGenItem.password,
                revealTimeoutMillis = revealTimeoutMillis,
                isLoading = passGenItem.isCalculating,
                isClickable = passGenItem.password != null,
                onClick = { passGenItem.password?.let(onCopyRequested) }
            )

            // Divider between Password Display and Strength Section
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )

            // Bottom: Combined Strength Progress & Word Label
            PasswordStrengthDisplay(
                strengthLevel = passGenItem.config.strengthLevel
            )
        }
    }
}

@Composable
private fun TypeBadge(
    typeBrief: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = typeBrief,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

