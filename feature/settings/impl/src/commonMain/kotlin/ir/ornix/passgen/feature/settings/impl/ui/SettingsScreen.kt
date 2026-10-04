package ir.ornix.passgen.feature.settings.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.model.AppLanguage
import ir.ornix.passgen.core.model.AppTheme
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.core.ui.settings_change_local_lock
import ir.ornix.passgen.core.ui.settings_fingerprints_subtitle_available
import ir.ornix.passgen.core.ui.settings_fingerprints_subtitle_unavailable
import ir.ornix.passgen.core.ui.settings_fingerprints_title
import ir.ornix.passgen.core.ui.settings_language_english
import ir.ornix.passgen.core.ui.settings_language_persian
import ir.ornix.passgen.core.ui.settings_language_system
import ir.ornix.passgen.core.ui.settings_language_title
import ir.ornix.passgen.core.ui.settings_local_lock_title
import ir.ornix.passgen.core.ui.settings_lock_active_password
import ir.ornix.passgen.core.ui.settings_lock_active_pattern
import ir.ornix.passgen.core.ui.settings_lock_active_pin
import ir.ornix.passgen.core.ui.settings_lock_none
import ir.ornix.passgen.core.ui.settings_section_appearance
import ir.ornix.passgen.core.ui.settings_section_verification_methods
import ir.ornix.passgen.core.ui.settings_theme_dark
import ir.ornix.passgen.core.ui.settings_theme_light
import ir.ornix.passgen.core.ui.settings_theme_system
import ir.ornix.passgen.core.ui.settings_theme_title
import ir.ornix.passgen.feature.settings.impl.presentation.SettingsIntent
import ir.ornix.passgen.feature.settings.impl.presentation.SettingsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateToSecretSetupClick: () -> Unit
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.secureContent()
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = stringResource(Res.string.settings_section_verification_methods),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        VerificationMethodRow(
            title = stringResource(Res.string.settings_fingerprints_title),
            subtitle = if (uiState.isBiometricAvailable) {
                stringResource(Res.string.settings_fingerprints_subtitle_available)
            } else {
                stringResource(Res.string.settings_fingerprints_subtitle_unavailable)
            },
            enabled = uiState.isBiometricAvailable,
            checked = uiState.isBiometricEnabled,
            onCheckedChange = { viewModel.dispatch(SettingsIntent.SetFingerprintEnabled(it)) }
        )

        VerificationMethodRow(
            title = stringResource(Res.string.settings_local_lock_title),
            subtitle = when (uiState.currentLocalAuthType) {
                LocalAuthType.PIN -> stringResource(Res.string.settings_lock_active_pin)
                LocalAuthType.PASSWORD -> stringResource(Res.string.settings_lock_active_password)
                LocalAuthType.PATTERN -> stringResource(Res.string.settings_lock_active_pattern)
                else -> stringResource(Res.string.settings_lock_none)
            },
            enabled = true,
            checked = uiState.currentLocalAuthType != LocalAuthType.NONE,
            onCheckedChange = {
                if (uiState.currentLocalAuthType == LocalAuthType.NONE)
                    onNavigateToSecretSetupClick()
                else
                    viewModel.dispatch(SettingsIntent.LocalAuthenticationDisabled)
            }
        )

        if (uiState.currentLocalAuthType != LocalAuthType.NONE) {
            TextButton(
                onClick = {
                    onNavigateToSecretSetupClick()
                }
            ) {
                Text(stringResource(Res.string.settings_change_local_lock))
            }
        }

        HorizontalDivider()

        Text(
            text = stringResource(Res.string.settings_section_appearance),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        ThemeSelectorRow(
            selectedTheme = uiState.selectedTheme,
            onThemeSelected = { viewModel.dispatch(SettingsIntent.SelectTheme(it)) }
        )

        LanguageSelectorRow(
            selectedLanguage = uiState.selectedLanguage,
            onLanguageSelected = { viewModel.dispatch(SettingsIntent.SelectLanguage(it)) }
        )
    }
}

@Composable
private fun VerificationMethodRow(
    title: String,
    subtitle: String,
    enabled: Boolean,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelectorRow(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val themeLabel = when (selectedTheme) {
        AppTheme.SYSTEM -> stringResource(Res.string.settings_theme_system)
        AppTheme.LIGHT -> stringResource(Res.string.settings_theme_light)
        AppTheme.DARK -> stringResource(Res.string.settings_theme_dark)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(Res.string.settings_theme_title),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = themeLabel,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                AppTheme.entries.forEach { theme ->
                    val label = when (theme) {
                        AppTheme.SYSTEM -> stringResource(Res.string.settings_theme_system)
                        AppTheme.LIGHT -> stringResource(Res.string.settings_theme_light)
                        AppTheme.DARK -> stringResource(Res.string.settings_theme_dark)
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (theme == selectedTheme) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onThemeSelected(theme)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelectorRow(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val languageLabel = when (selectedLanguage) {
        AppLanguage.SYSTEM -> stringResource(Res.string.settings_language_system)
        AppLanguage.ENGLISH -> stringResource(Res.string.settings_language_english)
        AppLanguage.PERSIAN -> stringResource(Res.string.settings_language_persian)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(Res.string.settings_language_title),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = languageLabel,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                AppLanguage.entries.forEach { language ->
                    val label = when (language) {
                        AppLanguage.SYSTEM -> stringResource(Res.string.settings_language_system)
                        AppLanguage.ENGLISH -> stringResource(Res.string.settings_language_english)
                        AppLanguage.PERSIAN -> stringResource(Res.string.settings_language_persian)
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (language == selectedLanguage) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onLanguageSelected(language)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}
