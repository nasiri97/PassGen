package ir.ornix.passgen.core.ui.component

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
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_cancel
import ir.ornix.passgen.core.ui.action_cannot_be_undone
import ir.ornix.passgen.core.ui.action_delete
import ir.ornix.passgen.core.ui.delete_item_confirm_msg_generic
import ir.ornix.passgen.core.ui.delete_item_confirm_msg_named
import ir.ornix.passgen.core.ui.delete_item_title
import ir.ornix.passgen.core.ui.security.secureContent
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmDeleteDialog(
    itemLabel: String? = null,
    onConfirmDelete: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val message = if (itemLabel.isNullOrBlank()) {
        stringResource(Res.string.delete_item_confirm_msg_generic)
    } else {
        stringResource(Res.string.delete_item_confirm_msg_named, itemLabel)
    }

    AlertDialog(
        modifier = modifier.secureContent(),
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
                text = stringResource(Res.string.delete_item_title),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = "$message\n${stringResource(Res.string.action_cannot_be_undone)}",
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
                Text(stringResource(Res.string.action_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.action_cancel))
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
