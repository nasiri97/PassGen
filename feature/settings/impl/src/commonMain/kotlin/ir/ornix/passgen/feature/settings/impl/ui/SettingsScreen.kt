package ir.ornix.passgen.feature.settings.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.feature.localauth.impl.secretsetup.ui.SecretSetupScreen
import ir.ornix.passgen.feature.settings.impl.presentation.SettingsIntent
import ir.ornix.passgen.feature.settings.impl.presentation.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {

    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isSettingPassInProgress) {
        SecretSetupScreen(
            modifier = modifier.secureContent(),
            onFinished = { viewModel.dispatch(SettingsIntent.SettingPasswordCompleted) }
        )
    } else {
        Column(
            modifier = modifier.secureContent()
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Verification methods",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            VerificationMethodRow(
                title = "Fingerprints",
                subtitle = if (uiState.isBiometricAvailable) "Unlock application with biometric recognition" else "Biometrics not available on this device",
                enabled = uiState.isBiometricAvailable,
                checked = uiState.isBiometricEnabled,
                onCheckedChange = { viewModel.dispatch(SettingsIntent.SetFingerprintEnabled(it)) }
            )

            VerificationMethodRow(
                title = "PIN, password, or pattern",
                subtitle = when (uiState.currentLocalAuthType) {
                    LocalAuthType.PIN -> "PIN lock is active"
                    LocalAuthType.PASSWORD -> "Password lock is active"
                    LocalAuthType.PATTERN -> "Pattern lock is active"
                    else -> "No local lock configured"
                },
                enabled = true,
                checked = uiState.currentLocalAuthType != LocalAuthType.NONE,
                onCheckedChange = {
                    if (uiState.currentLocalAuthType == LocalAuthType.NONE)
                        viewModel.dispatch(SettingsIntent.NavigateToSetPassword)
                    else
                        viewModel.dispatch(SettingsIntent.SetLocalAuthenticationEnabled(false))
                }
            )

            if (uiState.currentLocalAuthType != LocalAuthType.NONE) {
                TextButton(
                    onClick = { viewModel.dispatch(SettingsIntent.NavigateToSetPassword) }
                ) {
                    Text("Change local lock method")
                }
            }
        }
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
