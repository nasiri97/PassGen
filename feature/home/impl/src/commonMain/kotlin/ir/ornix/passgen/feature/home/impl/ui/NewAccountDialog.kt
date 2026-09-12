package ir.ornix.passgen.feature.home.impl.ui

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
import ir.ornix.passgen.core.designsystem.component.PasswordAndActions
import ir.ornix.passgen.core.model.Account


@Composable
fun NewAccountDialog(
    password: String,
    onSubmit: (account: Account) -> Unit,
    onDismissRequest: () -> Unit,
    copyPassword: (password: String) -> Unit,
    modifier: Modifier = Modifier
) {

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 4.dp
        ) {
            NewAccountContent(
                password = password,
                onSubmit = onSubmit,
                onCancel = onDismissRequest,
                copyPassword = copyPassword
            )
        }
    }
}

@Composable
private fun NewAccountContent(
    password: String,
    onSubmit: (Account) -> Unit,
    onCancel: () -> Unit,
    copyPassword: (password: String) -> Unit
) {

    var userName by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Create New Account",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        PasswordAndActions(
            password = password,
            copy = { copyPassword(password) }
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel")
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
                Text("Create")
            }
        }
    }
}
