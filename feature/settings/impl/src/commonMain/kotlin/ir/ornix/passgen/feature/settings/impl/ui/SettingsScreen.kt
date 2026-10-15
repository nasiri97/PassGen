package ir.ornix.passgen.feature.settings.impl.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.model.PasswordVisibilityTimeout
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.settings_change_local_lock
import ir.ornix.passgen.core.ui.settings_copy_subtitle
import ir.ornix.passgen.core.ui.settings_copy_title
import ir.ornix.passgen.core.ui.settings_copy_warning_cancel
import ir.ornix.passgen.core.ui.settings_copy_warning_confirm
import ir.ornix.passgen.core.ui.settings_copy_warning_message
import ir.ornix.passgen.core.ui.settings_copy_warning_title
import ir.ornix.passgen.core.ui.settings_fingerprints_subtitle_available
import ir.ornix.passgen.core.ui.settings_fingerprints_subtitle_unavailable
import ir.ornix.passgen.core.ui.settings_fingerprints_title
import ir.ornix.passgen.core.ui.settings_language_english
import ir.ornix.passgen.core.ui.settings_language_persian
import ir.ornix.passgen.core.ui.settings_language_subtitle
import ir.ornix.passgen.core.ui.settings_language_system
import ir.ornix.passgen.core.ui.settings_language_title
import ir.ornix.passgen.core.ui.settings_local_lock_title
import ir.ornix.passgen.core.ui.settings_lock_active_password
import ir.ornix.passgen.core.ui.settings_lock_active_pattern
import ir.ornix.passgen.core.ui.settings_lock_active_pin
import ir.ornix.passgen.core.ui.settings_lock_none
import ir.ornix.passgen.core.ui.settings_section_appearance
import ir.ornix.passgen.core.ui.settings_section_security_privacy
import ir.ornix.passgen.core.ui.settings_section_verification_methods
import ir.ornix.passgen.core.ui.settings_theme_dark
import ir.ornix.passgen.core.ui.settings_theme_light
import ir.ornix.passgen.core.ui.settings_theme_subtitle
import ir.ornix.passgen.core.ui.settings_theme_system
import ir.ornix.passgen.core.ui.settings_theme_title
import ir.ornix.passgen.core.ui.settings_timeout_10s
import ir.ornix.passgen.core.ui.settings_timeout_1m
import ir.ornix.passgen.core.ui.settings_timeout_20s
import ir.ornix.passgen.core.ui.settings_timeout_30s
import ir.ornix.passgen.core.ui.settings_timeout_5s
import ir.ornix.passgen.core.ui.settings_timeout_disabled
import ir.ornix.passgen.core.ui.settings_visibility_timeout_subtitle
import ir.ornix.passgen.core.ui.settings_visibility_timeout_title
import ir.ornix.passgen.feature.settings.impl.presentation.SettingsIntent
import ir.ornix.passgen.feature.settings.impl.presentation.SettingsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private enum class ActiveDialog {
    TIMEOUT,
    THEME,
    LANGUAGE
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateToSecretSetupClick: () -> Unit
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var activeDialog by remember { mutableStateOf<ActiveDialog?>(null) }

    if (uiState.showCopyWarningDialog) {
        CopyWarningDialog(
            onConfirm = { viewModel.dispatch(SettingsIntent.ConfirmEnableCopy) },
            onDismiss = { viewModel.dispatch(SettingsIntent.DismissCopyWarningDialog) }
        )
    }

    when (activeDialog) {
        ActiveDialog.TIMEOUT -> {
            SettingSelectionDialog(
                title = stringResource(Res.string.settings_visibility_timeout_title),
                icon = Icons.Outlined.Timer,
                options = PasswordVisibilityTimeout.entries,
                selectedOption = uiState.selectedVisibilityTimeout,
                optionLabel = { timeout -> getTimeoutLabel(timeout) },
                onOptionSelected = { viewModel.dispatch(SettingsIntent.SelectVisibilityTimeout(it)) },
                onDismissRequest = { activeDialog = null }
            )
        }

        ActiveDialog.THEME -> {
            SettingSelectionDialog(
                title = stringResource(Res.string.settings_theme_title),
                icon = Icons.Outlined.Palette,
                options = AppTheme.entries,
                selectedOption = uiState.selectedTheme,
                optionLabel = { theme -> getThemeLabel(theme) },
                onOptionSelected = { viewModel.dispatch(SettingsIntent.SelectTheme(it)) },
                onDismissRequest = { activeDialog = null }
            )
        }

        ActiveDialog.LANGUAGE -> {
            SettingSelectionDialog(
                title = stringResource(Res.string.settings_language_title),
                icon = Icons.Outlined.Language,
                options = AppLanguage.entries,
                selectedOption = uiState.selectedLanguage,
                optionLabel = { language -> getLanguageLabel(language) },
                onOptionSelected = { viewModel.dispatch(SettingsIntent.SelectLanguage(it)) },
                onDismissRequest = { activeDialog = null }
            )
        }

        null -> Unit
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Verification Methods
        SectionCard(
            sectionTitle = stringResource(Res.string.settings_section_verification_methods),
            sectionIcon = Icons.Outlined.Shield
        ) {
            SettingSwitchRow(
                title = stringResource(Res.string.settings_fingerprints_title),
                subtitle = if (uiState.isBiometricAvailable) {
                    stringResource(Res.string.settings_fingerprints_subtitle_available)
                } else {
                    stringResource(Res.string.settings_fingerprints_subtitle_unavailable)
                },
                icon = Icons.Outlined.Fingerprint,
                checked = uiState.isBiometricEnabled,
                enabled = uiState.isBiometricAvailable,
                onCheckedChange = { viewModel.dispatch(SettingsIntent.SetFingerprintEnabled(it)) }
            )

            ItemDivider()

            SettingLocalLockRow(
                currentLocalAuthType = uiState.currentLocalAuthType,
                onToggleLocalAuth = { enabled ->
                    if (enabled) {
                        onNavigateToSecretSetupClick()
                    } else {
                        viewModel.dispatch(SettingsIntent.LocalAuthenticationDisabled)
                    }
                },
                onChangeLocalLock = onNavigateToSecretSetupClick
            )
        }

        // Section 2: Security & Privacy
        SectionCard(
            sectionTitle = stringResource(Res.string.settings_section_security_privacy),
            sectionIcon = Icons.Outlined.Security
        ) {
            SettingSwitchRow(
                title = stringResource(Res.string.settings_copy_title),
                subtitle = stringResource(Res.string.settings_copy_subtitle),
                icon = Icons.Outlined.ContentCopy,
                checked = uiState.isCopyEnabled,
                onCheckedChange = { viewModel.dispatch(SettingsIntent.RequestSetCopyEnabled(it)) }
            )

            ItemDivider()

            SettingSelectionRow(
                title = stringResource(Res.string.settings_visibility_timeout_title),
                subtitle = stringResource(Res.string.settings_visibility_timeout_subtitle),
                icon = Icons.Outlined.Timer,
                selectedValueLabel = getTimeoutLabel(uiState.selectedVisibilityTimeout),
                onClick = { activeDialog = ActiveDialog.TIMEOUT }
            )
        }

        // Section 3: Appearance & Language
        SectionCard(
            sectionTitle = stringResource(Res.string.settings_section_appearance),
            sectionIcon = Icons.Outlined.DarkMode
        ) {
            SettingSelectionRow(
                title = stringResource(Res.string.settings_theme_title),
                subtitle = stringResource(Res.string.settings_theme_subtitle),
                icon = Icons.Outlined.Palette,
                selectedValueLabel = getThemeLabel(uiState.selectedTheme),
                onClick = { activeDialog = ActiveDialog.THEME }
            )

            ItemDivider()

            SettingSelectionRow(
                title = stringResource(Res.string.settings_language_title),
                subtitle = stringResource(Res.string.settings_language_subtitle),
                icon = Icons.Outlined.Language,
                selectedValueLabel = getLanguageLabel(uiState.selectedLanguage),
                onClick = { activeDialog = ActiveDialog.LANGUAGE }
            )
        }
    }
}

@Composable
private fun SectionCard(
    sectionTitle: String,
    sectionIcon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = sectionIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = sectionTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            content()
        }
    }
}

@Composable
private fun SettingIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SettingIcon(imageVector = icon)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
private fun SettingLocalLockRow(
    currentLocalAuthType: LocalAuthType,
    onToggleLocalAuth: (Boolean) -> Unit,
    onChangeLocalLock: () -> Unit
) {
    val isEnabled = currentLocalAuthType != LocalAuthType.NONE
    val subtitle = when (currentLocalAuthType) {
        LocalAuthType.PIN -> stringResource(Res.string.settings_lock_active_pin)
        LocalAuthType.PASSWORD -> stringResource(Res.string.settings_lock_active_password)
        LocalAuthType.PATTERN -> stringResource(Res.string.settings_lock_active_pattern)
        else -> stringResource(Res.string.settings_lock_none)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleLocalAuth(!isEnabled) }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SettingIcon(imageVector = Icons.Outlined.Lock)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.settings_local_lock_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggleLocalAuth
            )
        }

        AnimatedVisibility(
            visible = isEnabled,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 70.dp, end = 16.dp, bottom = 12.dp)
            ) {
                OutlinedButton(
                    onClick = onChangeLocalLock,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(Res.string.settings_change_local_lock),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingSelectionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selectedValueLabel: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SettingIcon(imageVector = icon)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = selectedValueLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ItemDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 70.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    )
}

@Composable
private fun <T> SettingSelectionDialog(
    title: String,
    icon: ImageVector,
    options: List<T>,
    selectedOption: T,
    optionLabel: @Composable (T) -> String,
    onOptionSelected: (T) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                options.forEach { option ->
                    val isSelected = option == selectedOption
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .selectable(
                                selected = isSelected,
                                onClick = {
                                    onOptionSelected(option)
                                    onDismissRequest()
                                },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null
                        )
                        Text(
                            text = optionLabel(option),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.settings_copy_warning_cancel))
            }
        }
    )
}

@Composable
private fun CopyWarningDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        title = {
            Text(
                text = stringResource(Res.string.settings_copy_warning_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(Res.string.settings_copy_warning_message),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(Res.string.settings_copy_warning_confirm),
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.settings_copy_warning_cancel))
            }
        }
    )
}

@Composable
private fun getTimeoutLabel(timeout: PasswordVisibilityTimeout): String = when (timeout) {
    PasswordVisibilityTimeout.DISABLED -> stringResource(Res.string.settings_timeout_disabled)
    PasswordVisibilityTimeout.SECONDS_5 -> stringResource(Res.string.settings_timeout_5s)
    PasswordVisibilityTimeout.SECONDS_10 -> stringResource(Res.string.settings_timeout_10s)
    PasswordVisibilityTimeout.SECONDS_20 -> stringResource(Res.string.settings_timeout_20s)
    PasswordVisibilityTimeout.SECONDS_30 -> stringResource(Res.string.settings_timeout_30s)
    PasswordVisibilityTimeout.MINUTE_1 -> stringResource(Res.string.settings_timeout_1m)
}

@Composable
private fun getThemeLabel(theme: AppTheme): String = when (theme) {
    AppTheme.SYSTEM -> stringResource(Res.string.settings_theme_system)
    AppTheme.LIGHT -> stringResource(Res.string.settings_theme_light)
    AppTheme.DARK -> stringResource(Res.string.settings_theme_dark)
}

@Composable
private fun getLanguageLabel(language: AppLanguage): String = when (language) {
    AppLanguage.SYSTEM -> stringResource(Res.string.settings_language_system)
    AppLanguage.ENGLISH -> stringResource(Res.string.settings_language_english)
    AppLanguage.PERSIAN -> stringResource(Res.string.settings_language_persian)
}
