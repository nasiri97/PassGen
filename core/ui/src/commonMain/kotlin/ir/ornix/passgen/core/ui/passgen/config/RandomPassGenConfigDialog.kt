package ir.ornix.passgen.core.ui.passgen.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_close
import ir.ornix.passgen.core.ui.label_output_encoder_type
import ir.ornix.passgen.core.ui.label_password_length
import ir.ornix.passgen.core.ui.section_generating_settings
import ir.ornix.passgen.core.ui.section_postprocess_settings
import ir.ornix.passgen.core.ui.security.secureContent
import org.jetbrains.compose.resources.stringResource

@Composable
fun RandomPassGenConfigDialog(
    config: RandomPassGenConfig,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier.secureContent(),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.action_close))
            }
        },
        title = {
            Text(
                text = config.name,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = stringResource(Res.string.section_generating_settings),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                ConfigColumn(
                    label = stringResource(Res.string.label_output_encoder_type),
                    name = config.passEncoder.fullName,
                    description = null
                )

                HorizontalDivider()

                Text(
                    text = stringResource(Res.string.section_postprocess_settings),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                ConfigRow(
                    label = stringResource(Res.string.label_password_length),
                    value = config.passwordLength.toString()
                )
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun ConfigColumn(label: String, name: String, description: String?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            style = MaterialTheme.typography.bodyMedium,
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(name)
                }

                description?.let {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(" $it")
                    }
                }
            }
        )
    }
}

@Composable
private fun ConfigRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
