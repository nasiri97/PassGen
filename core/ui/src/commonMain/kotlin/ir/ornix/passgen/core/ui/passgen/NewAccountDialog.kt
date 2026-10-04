package ir.ornix.passgen.core.ui.passgen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ir.ornix.passgen.core.model.Account
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_cancel
import ir.ornix.passgen.core.ui.action_create
import ir.ornix.passgen.core.ui.component.PasswordAndActions
import ir.ornix.passgen.core.ui.create_new_account_title
import ir.ornix.passgen.core.ui.label_username
import ir.ornix.passgen.core.ui.security.secureContent
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewAccountDialog(
    password: Password,
    onSubmit: (account: Account) -> Unit,
    onDismissRequest: () -> Unit,
    onCopyRequested: (Password) -> Unit,
    modifier: Modifier = Modifier
) {

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier.secureContent()
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 4.dp
        ) {
            NewAccountContent(
                password = password,
                onSubmit = onSubmit,
                onCancel = onDismissRequest,
                onCopyRequested = onCopyRequested
            )
        }
    }
}

@Composable
private fun NewAccountContent(
    password: Password,
    onSubmit: (Account) -> Unit,
    onCancel: () -> Unit,
    onCopyRequested: (Password) -> Unit
) {

    var userName by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = stringResource(Res.string.create_new_account_title),
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text(stringResource(Res.string.label_username)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        PasswordAndActions(
            password = password,
            onCopyRequested = onCopyRequested
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(Res.string.action_cancel))
            }

            Spacer(Modifier.width(8.dp))

            Button(
                enabled = userName.isNotBlank(),
                onClick = {
                    onSubmit(
                        Account(
                            id = 0,
                            username = userName,
                            password = password
                        )
                    )
                }
            ) {
                Text(stringResource(Res.string.action_create))
            }
        }
    }
}
