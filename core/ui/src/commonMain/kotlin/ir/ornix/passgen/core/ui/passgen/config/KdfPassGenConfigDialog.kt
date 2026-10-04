package ir.ornix.passgen.core.ui.passgen.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PreprocessConfig
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_close
import ir.ornix.passgen.core.ui.label_hashing_algorithm
import ir.ornix.passgen.core.ui.label_output_encoder_type
import ir.ornix.passgen.core.ui.label_password_length
import ir.ornix.passgen.core.ui.section_generating_settings
import ir.ornix.passgen.core.ui.section_postprocess_settings
import ir.ornix.passgen.core.ui.section_preprocess_settings
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.core.ui.setting_collapse_spaces
import ir.ornix.passgen.core.ui.setting_to_lowercase
import ir.ornix.passgen.core.ui.setting_trim_spaces
import org.jetbrains.compose.resources.stringResource

@Composable
fun KdfPassGenConfigDialog(
    config: KdfPassGenConfig,
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
                    text = stringResource(Res.string.section_preprocess_settings),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                PreprocessSettings(config.preprocessConfig)

                HorizontalDivider()

                Text(
                    text = stringResource(Res.string.section_generating_settings),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                ConfigColumn(
                    label = stringResource(Res.string.label_hashing_algorithm),
                    name = config.inputHasher.fullName,
                    description = config.inputHasher.description
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

@Composable
private fun PreprocessSettings(config: PreprocessConfig) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SettingItem(
            stringResource(Res.string.setting_trim_spaces),
            config.trimLeadingAndTrailingSpaces
        )
        SettingItem(
            stringResource(Res.string.setting_collapse_spaces),
            config.collapseMultipleSpaces
        )
        SettingItem(stringResource(Res.string.setting_to_lowercase), config.convertToLowercase)
    }
}

@Composable
private fun SettingItem(label: String, enabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            imageVector = if (enabled) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}
