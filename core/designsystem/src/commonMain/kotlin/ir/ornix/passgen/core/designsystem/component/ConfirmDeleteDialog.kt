package ir.ornix.passgen.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ir.ornix.passgen.core.designsystem.BothPreview

@Composable
fun ConfirmDeleteDialog(
    itemLabel: String? = null,
    onConfirmDelete: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val message = if (itemLabel.isNullOrBlank()) {
        "Are you sure you want to delete this item?"
    } else {
        "Are you sure you want to delete \"$itemLabel\"?"
    }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                imageVector = Icons.Outlined.DeleteForever,
                contentDescription = null, // decorative
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Delete item",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = "$message\nThis action can’t be undone.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}


@BothPreview
@Composable
private fun ConfirmDeleteDialogPreview() {
    ConfirmDeleteDialog(
        itemLabel = "A1",
        onConfirmDelete = {},
        onDismissRequest = {})
}