package ir.ornix.passgen.feature.config.impl.kdf.ui.master_key_step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_cancel
import ir.ornix.passgen.core.ui.action_next
import ir.ornix.passgen.core.ui.error_master_keys_mismatch
import ir.ornix.passgen.core.ui.error_max_length
import ir.ornix.passgen.core.ui.label_confirm_master_key
import ir.ornix.passgen.core.ui.label_master_key
import ir.ornix.passgen.core.ui.master_key_explanation
import ir.ornix.passgen.core.ui.security.PasswordTextField
import ir.ornix.passgen.core.ui.setup_master_key_title
import ir.ornix.passgen.feature.config.impl.kdf.presentation.MasterKeyValidation
import org.jetbrains.compose.resources.stringResource

private const val MAX_MASTER_KEY_CHARS = 256

@Composable
internal fun MasterKeyStepContent(
    validation: MasterKeyValidation,
    revealTimeoutMillis: Long,
    onMasterKeyChange: (CharArray) -> Unit,
    onConfirmMasterKeyChange: (CharArray) -> Unit,
    acknowledged: Boolean,
    onAcknowledgedChange: (Boolean) -> Unit,
    onNext: () -> Unit,
    onCancel: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(Res.string.setup_master_key_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(Res.string.master_key_explanation),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        MasterKeyNotice(
            acknowledged = acknowledged,
            onAcknowledgedChange = onAcknowledgedChange
        )

        val isAtLimit = validation.masterKeyLength >= MAX_MASTER_KEY_CHARS

        PasswordTextField(
            onValueChange = onMasterKeyChange,
            revealTimeoutMillis = revealTimeoutMillis,
            maxLength = MAX_MASTER_KEY_CHARS,
            label = { Text(stringResource(Res.string.label_master_key)) },
            contentDescription = stringResource(Res.string.label_master_key),
            isError = isAtLimit,
            supportingText = {
                if (isAtLimit) {
                    Text(stringResource(Res.string.error_max_length, MAX_MASTER_KEY_CHARS))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            testTag = "master_key_input"
        )

        PasswordTextField(
            onValueChange = onConfirmMasterKeyChange,
            revealTimeoutMillis = revealTimeoutMillis,
            maxLength = MAX_MASTER_KEY_CHARS,
            label = { Text(stringResource(Res.string.label_confirm_master_key)) },
            contentDescription = stringResource(Res.string.label_confirm_master_key),
            isError = validation.shouldShowConfirmError,
            supportingText = if (validation.shouldShowConfirmError) {
                {
                    Text(
                        stringResource(Res.string.error_master_keys_mismatch),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            testTag = "confirm_master_key_input"
        )

        MasterKeyValidationCard(validation = validation)

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(Res.string.action_cancel))
            }

            Spacer(Modifier.width(8.dp))

            Button(
                modifier = Modifier.testTag("next_button"),
                enabled = validation.isValid && acknowledged,
                onClick = onNext
            ) {
                Text(stringResource(Res.string.action_next))
            }
        }
    }
}
