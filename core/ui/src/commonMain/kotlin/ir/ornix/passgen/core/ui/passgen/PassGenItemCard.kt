package ir.ornix.passgen.core.ui.passgen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.model.Account
import ir.ornix.passgen.core.model.PassGenItem
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import ir.ornix.passgen.core.ui.component.ConfirmDeleteDialog
import ir.ornix.passgen.core.ui.component.PasswordAndActions
import ir.ornix.passgen.core.ui.util.strengthColor
import kotlinx.coroutines.launch

private val passCardCornerRadius = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassGenItemCard(
    passGenItem: PassGenItem,
    onRemove: () -> Unit,
    addNewAccount: (account: Account) -> Unit,
    showPassGenInfoDialog: (PassGenConfig) -> Unit,
    copy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var showNewAccountDialog by remember { mutableStateOf(false) }

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
                val color =
                    if (direction == SwipeToDismissBoxValue.StartToEnd) Color.Blue else Color.Red
                val icon =
                    if (direction == SwipeToDismissBoxValue.StartToEnd) Icons.Default.Update else Icons.Default.Delete
                val alignment =
                    if (direction == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd

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
        Content(
            passGenItem = passGenItem,
            showPassGenInfoDialog = { showPassGenInfoDialog(passGenItem.config) },
            copy = copy
        )
    }
}

@Composable
private fun Content(
    passGenItem: PassGenItem,
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
                        text = passGenItem.config.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = passGenItem.config.typeBrief,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Text(
                    text = passGenItem.config.strengthLevel.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = passGenItem.config.strengthColor(),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            PasswordAndActions(
                password = passGenItem.password?.value,
                copy = copy,
                isLoading = passGenItem.isCalculating,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { passGenItem.config.strengthLevel.ratio },
                modifier = Modifier.fillMaxWidth(),
                color = passGenItem.config.strengthColor()
            )
        }
    }
}
