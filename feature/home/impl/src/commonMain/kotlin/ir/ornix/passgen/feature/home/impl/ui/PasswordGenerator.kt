package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.designsystem.component.ConfirmDeleteDialog
import ir.ornix.passgen.core.designsystem.component.PasswordAndActions
import ir.ornix.passgen.core.designsystem.utils.strengthColor
import ir.ornix.passgen.core.domain.model.Account
import ir.ornix.passgen.passwordgenerator.model.KDFPassGenConfig
import ir.ornix.passgen.passwordgenerator.model.Password
import ir.ornix.passgen.passwordgenerator.model.strengthLabel
import kotlinx.coroutines.launch

private val passCardCornerRadius = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeablePasswordGenerator(
    passGenConfig: KDFPassGenConfig,
    password: Password?,
    isLoading: Boolean,
    onRemove: () -> Unit,
    addNewAccount: (account: Account) -> Unit,
    showPassGenInfoDialog: (KDFPassGenConfig) -> Unit,
    copy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var showNewAccountDialog by remember { mutableStateOf(false) }

    if (showConfirmDeleteDialog) {
        ConfirmDeleteDialog(
            itemLabel = passGenConfig.name,
            onConfirmDelete = {
                onRemove()
                showConfirmDeleteDialog = false
            },
            onDismissRequest = { showConfirmDeleteDialog = false }
        )
    }

    if (showNewAccountDialog && password != null && password.value.isNotEmpty()) {
        NewAccountDialog(
            password = password.value,
            onSubmit = { newAccount ->
                addNewAccount(newAccount)
                showNewAccountDialog = false
            },
            onDismissRequest = { showNewAccountDialog = false },
            copyPassword = copy
        )
    }

    val swipeState = rememberSwipeToDismissBoxState()
    val scope = rememberCoroutineScope()

    SwipeToDismissBox(
        state = swipeState,
        onDismiss = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    showNewAccountDialog = true
                    scope.launch { swipeState.reset() }
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    showConfirmDeleteDialog = true
                    scope.launch { swipeState.reset() }
                }
                SwipeToDismissBoxValue.Settled -> {}
            }
        },
        modifier = modifier.fillMaxWidth(),
        backgroundContent = {
            val direction = swipeState.dismissDirection
            if (direction != SwipeToDismissBoxValue.Settled) {
                val color = if (direction == SwipeToDismissBoxValue.StartToEnd) Color.Blue else Color.Red
                val icon = if (direction == SwipeToDismissBoxValue.StartToEnd) Icons.Default.Update else Icons.Default.Delete
                val alignment = if (direction == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color, shape = RoundedCornerShape(passCardCornerRadius))
                        .padding(horizontal = 24.dp),
                    contentAlignment = alignment
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White)
                }
            }
        }
    ) {
        PasswordGenerator(
            passGenConfig = passGenConfig,
            password = password,
            isLoading = isLoading,
            showPassGenInfoDialog = { showPassGenInfoDialog(passGenConfig) },
            copy = copy
        )
    }
}

@Composable
private fun PasswordGenerator(
    passGenConfig: KDFPassGenConfig,
    password: Password?,
    isLoading: Boolean,
    showPassGenInfoDialog: () -> Unit,
    copy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(passCardCornerRadius)),
        shape = RoundedCornerShape(passCardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .defaultMinSize(minHeight = 140.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { showPassGenInfoDialog() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = passGenConfig.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = passGenConfig.typeBrief,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                if (!isLoading && password != null) {
                    Text(
                        text = password.strengthLabel(),
                        style = MaterialTheme.typography.labelMedium,
                        color = password.strengthColor(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            PasswordAndActions(
                password = password?.value,
                copy = copy,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (password != null && !isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { password.strengthRatio },
                    modifier = Modifier.fillMaxWidth(),
                    color = password.strengthColor()
                )
            }
        }
    }
}
