package ir.ornix.passgen.core.ui.passgen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import ir.ornix.passgen.core.model.passgenconfig.RandomPassGenConfig
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_close
import ir.ornix.passgen.core.ui.label_entropy_size
import ir.ornix.passgen.core.ui.label_hashing_algorithm
import ir.ornix.passgen.core.ui.label_length
import ir.ornix.passgen.core.ui.label_output_encoder_type
import ir.ornix.passgen.core.ui.label_type
import ir.ornix.passgen.core.ui.password_strength
import ir.ornix.passgen.core.ui.section_generating_settings
import ir.ornix.passgen.core.ui.section_info
import ir.ornix.passgen.core.ui.setting_collapse_spaces
import ir.ornix.passgen.core.ui.setting_to_lowercase
import ir.ornix.passgen.core.ui.setting_trim_spaces
import ir.ornix.passgen.core.ui.state_disabled
import ir.ornix.passgen.core.ui.state_enabled
import ir.ornix.passgen.core.ui.title_context_input_normalization
import ir.ornix.passgen.core.ui.type_kdf
import ir.ornix.passgen.core.ui.type_random
import ir.ornix.passgen.core.ui.util.label
import ir.ornix.passgen.core.ui.util.strengthColor
import ir.ornix.passgen.core.ui.value_bytes
import ir.ornix.passgen.core.ui.value_characters
import ir.ornix.passgen.core.ui.value_words
import org.jetbrains.compose.resources.stringResource

@Composable
fun PassGenConfigDialog(
    config: PassGenConfig,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth(0.92f),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp
        ) {
            Column(Modifier.padding(top = 12.dp, bottom = 16.dp)) {

                DialogHeader(
                    name = config.name,
                    onClose = onDismiss,
                    modifier = Modifier.padding(start = 24.dp, end = 12.dp)
                )

                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoSection(config)

                    when (config) {
                        is KdfPassGenConfig -> KdfSections(config)
                        is RandomPassGenConfig -> RandomSections(config)
                    }
                }
            }
        }
    }
}

/* ------------------------------ Type-specific ------------------------------ */

@Composable
private fun KdfSections(config: KdfPassGenConfig) {
    ConfigSection(
        title = stringResource(Res.string.title_context_input_normalization),
        icon = Icons.Default.Build
    ) {
        ToggleRow(
            stringResource(Res.string.setting_trim_spaces),
            config.preprocessConfig.trimLeadingAndTrailingSpaces
        )
        ToggleRow(
            stringResource(Res.string.setting_collapse_spaces),
            config.preprocessConfig.collapseMultipleSpaces
        )
        ToggleRow(
            stringResource(Res.string.setting_to_lowercase),
            config.preprocessConfig.convertToLowercase
        )
    }

    ConfigSection(
        title = stringResource(Res.string.section_generating_settings),
        icon = Icons.Default.Lock
    ) {
        DetailBlock(
            label = stringResource(Res.string.label_hashing_algorithm),
            value = config.inputHasher.fullName,
            description = config.inputHasher.description
        )
        DetailBlock(
            label = stringResource(Res.string.label_output_encoder_type),
            value = config.passEncoder.fullName
        )
    }
}

@Composable
private fun RandomSections(config: RandomPassGenConfig) {
    ConfigSection(
        title = stringResource(Res.string.section_generating_settings),
        icon = Icons.Default.Lock
    ) {
        DetailBlock(
            label = stringResource(Res.string.label_output_encoder_type),
            value = config.passEncoder.fullName
        )
    }
}

/* -------------------------------- Building blocks -------------------------------- */

@Composable
private fun DialogHeader(
    name: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.action_close),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ConfigSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            content()
        }
    }
}

@Composable
private fun DetailBlock(label: String, value: String, description: String? = null) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ToggleRow(label: String, enabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        val container = if (enabled) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.errorContainer
        val content = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onErrorContainer

        Surface(shape = CircleShape, color = container, modifier = Modifier.size(24.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (enabled) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = stringResource(
                        if (enabled) Res.string.state_enabled else Res.string.state_disabled
                    ),
                    tint = content,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun Pill(
    text: String,
    container: Color,
    content: Color
) {
    Surface(shape = CircleShape, color = container) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = content,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun InfoSection(config: PassGenConfig) {
    val strengthColor = config.strengthLevel.strengthColor()

    ConfigSection(
        title = stringResource(Res.string.section_info),
        icon = Icons.Default.Info
    ) {
        TrailingInfoRow(label = stringResource(Res.string.label_type)) {
            Pill(
                text = stringResource(
                    when (config) {
                        is KdfPassGenConfig -> Res.string.type_kdf
                        is RandomPassGenConfig -> Res.string.type_random
                    }
                ),
                container = MaterialTheme.colorScheme.tertiaryContainer,
                content = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }

        (config.passEncoder as? SeedPassEncoder)?.let {
            SimpleInfoRow(
                label = stringResource(Res.string.label_length),
                value = stringResource(Res.string.value_words, it.wordCount)
            )
        }

        (config.passwordLength)?.let {
            SimpleInfoRow(
                label = stringResource(Res.string.label_length),
                value = stringResource(Res.string.value_characters, it)
            )
        }

        SimpleInfoRow(
            label = stringResource(Res.string.label_entropy_size),
            value = stringResource(Res.string.value_bytes, config.entropyByteSize)
        )

        TrailingInfoRow(label = stringResource(Res.string.password_strength)) {
            StrengthBadge(
                text = config.strengthLevel.label(),
                color = strengthColor
            )
        }

        PasswordStrengthProgressIndicator(
            strengthLevel = config.strengthLevel
        )
    }
}


@Composable
private fun TrailingInfoRow(label: String, trailing: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        trailing()
    }
}

@Composable
private fun SimpleInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun StrengthBadge(text: String, color: Color) {
    Surface(
        shape = CircleShape,
        color = color.copy(alpha = 0.14f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}