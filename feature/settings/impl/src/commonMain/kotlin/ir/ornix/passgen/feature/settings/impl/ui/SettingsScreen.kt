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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.feature.localauth.impl.secretsetup.SecretSetupScreen
import ir.ornix.passgen.feature.settings.impl.presentation.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen() {

    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var settingPass: Boolean by remember { mutableStateOf(false) }

    if (settingPass) {
        SecretSetupScreen(onFinished = { settingPass = false })
    } else {
        Column(
            modifier = Modifier
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
                onCheckedChange = { viewModel.toggleBiometricSetting(it) }
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
                    if (uiState.currentLocalAuthType == LocalAuthType.NONE) settingPass = true
                    else viewModel.toggleLocalAuthSetting(it)
                }
            )

            if (uiState.currentLocalAuthType != LocalAuthType.NONE) {
                TextButton(
                    onClick = { settingPass = true }
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
