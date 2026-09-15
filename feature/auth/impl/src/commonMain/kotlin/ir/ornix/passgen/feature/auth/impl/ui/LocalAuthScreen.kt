package ir.ornix.passgen.feature.auth.impl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.feature.auth.impl.presentation.LocalAuthViewModel
import ir.ornix.passgen.feature.auth.impl.presentation.SetupStage
import ir.ornix.passgen.feature.auth.impl.ui.components.PasswordLockView
import ir.ornix.passgen.feature.auth.impl.ui.components.PatternLockView
import ir.ornix.passgen.feature.auth.impl.ui.components.PinLockView
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LocalAuthScreen(
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LocalAuthViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onAuthenticated()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.isFirstLaunch) {
                Text(
                    text = "Application Security",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))

                when (uiState.setupStage) {
                    SetupStage.CHOOSE_TYPE -> {
                        Text(
                            text = "Set a local lock method or skip to biometric setup",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(32.dp))
                        Button(
                            onClick = { viewModel.selectSetupType(LocalAuthType.PIN) },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("Setup PIN")
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.selectSetupType(LocalAuthType.PASSWORD) },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("Setup Text Password")
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.selectSetupType(LocalAuthType.PATTERN) },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text("Setup Pattern Lock")
                        }
                        Spacer(Modifier.height(32.dp))
                        TextButton(onClick = { viewModel.selectSetupType(LocalAuthType.NONE) }) {
                            Text("Skip PIN/Pattern")
                        }
                    }

                    SetupStage.ENTER_SECRET -> {
                        val label = when (uiState.selectedSetupType) {
                            LocalAuthType.PIN -> "Enter new 4-digit PIN"
                            LocalAuthType.PASSWORD -> "Enter new Text Password"
                            else -> "Draw your secret pattern"
                        }
                        Text(text = label, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(24.dp))

                        if (uiState.selectedSetupType == LocalAuthType.PIN) {
                            PinLockView(
                                onPinCompleted = {
                                    viewModel.handleSecretInput(it)
                                }
                            )
                        } else if (uiState.selectedSetupType == LocalAuthType.PASSWORD) {
                            PasswordLockView(onPasswordSubmitted = {
                                viewModel.handleSecretInput(it)
                            })
                        } else {
                            Box(Modifier.size(300.dp)) {
                                PatternLockView(onPatternCompleted = {
                                    viewModel.handleSecretInput(
                                        it
                                    )
                                })
                            }
                        }
                    }

                    SetupStage.CONFIRM_SECRET -> {
                        val label = when (uiState.selectedSetupType) {
                            LocalAuthType.PIN -> "Confirm your 4-digit PIN"
                            LocalAuthType.PASSWORD -> "Confirm your Text Password"
                            else -> "Draw pattern again to confirm"
                        }
                        Text(text = label, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(24.dp))

                        if (uiState.selectedSetupType == LocalAuthType.PIN) {
                            PinLockView(onPinCompleted = {
                                viewModel.handleSecretInput(it)
                            })
                        } else if (uiState.selectedSetupType == LocalAuthType.PASSWORD) {
                            PasswordLockView(
                                onPasswordSubmitted = {
                                    viewModel.handleSecretInput(it)
                                }
                            )
                        } else {
                            Box(Modifier.size(300.dp)) {
                                PatternLockView(onPatternCompleted = {
                                    viewModel.handleSecretInput(
                                        it
                                    )
                                })
                            }
                        }
                    }

                    SetupStage.BIOMETRIC_SETUP -> {
                        Text(
                            text = "Biometric Authentication",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Would you like to use fingerprints to unlock the app?",
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(24.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Enable Fingerprints")
                            Spacer(Modifier.width(16.dp))
                            Switch(
                                checked = uiState.isBiometricEnabled,
                                onCheckedChange = { viewModel.setBiometricInSetup(it) }
                            )
                        }
                        Spacer(Modifier.height(48.dp))
                        Button(onClick = { viewModel.completeSetup() }) {
                            Text("Finish Setup")
                        }
                    }
                }
            } else {
                // Application unlocking gate
                Text(
                    text = "Application Locked",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(24.dp))

                when {
                    uiState.currentLocalAuthType == LocalAuthType.PIN -> {
                        PinLockView(
                            onPinCompleted = {
                                viewModel.handleSecretInput(it)
                            }
                        )
                    }

                    uiState.currentLocalAuthType == LocalAuthType.PASSWORD -> {
                        PasswordLockView(onPasswordSubmitted = {
                            viewModel.handleSecretInput(it)
                        })
                    }

                    uiState.currentLocalAuthType == LocalAuthType.PATTERN -> {
                        Box(Modifier.size(300.dp)) {
                            PatternLockView(onPatternCompleted = { viewModel.handleSecretInput(it) })
                        }
                    }

                    uiState.isBiometricEnabled -> {

                        LaunchedEffect(Unit) {
                            viewModel.triggerBiometricPrompt()
                        }

                        Spacer(Modifier.height(24.dp))
                        IconButton(
                            onClick = { viewModel.triggerBiometricPrompt() },
                            modifier = Modifier
                                .size(64.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric Login",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        TextButton(onClick = { viewModel.triggerBiometricPrompt() }) {
                            Text("Use Fingerprint")
                        }
                    }
                }
            }

            uiState.error?.let { err ->
                Spacer(Modifier.height(16.dp))
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}