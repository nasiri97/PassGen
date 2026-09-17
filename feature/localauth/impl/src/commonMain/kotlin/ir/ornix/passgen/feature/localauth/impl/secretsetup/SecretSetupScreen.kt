package ir.ornix.passgen.feature.localauth.impl.secretsetup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.feature.localauth.impl.components.PasswordLockView
import ir.ornix.passgen.feature.localauth.impl.components.PatternLockView
import ir.ornix.passgen.feature.localauth.impl.components.PinLockView
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SecretSetupScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel: SecretSetupViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier.secureContent().fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

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
                        text = "Set a local lock method",
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
                    TextButton(onClick = {
                        viewModel.cancelSetup()
                    }) {
                        Text("Skip")
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

                    when (uiState.selectedSetupType) {
                        LocalAuthType.PIN -> {
                            PinLockView(onPinCompleted = { viewModel.handleSecretInput(it) })
                        }

                        LocalAuthType.PASSWORD -> {
                            PasswordLockView(
                                onPasswordSubmitted = { viewModel.handleSecretInput(it) }
                            )
                        }

                        else -> {
                            Box(Modifier.size(300.dp)) {
                                PatternLockView(
                                    onPatternCompleted = { viewModel.handleSecretInput(it) }
                                )
                            }
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

                    when (uiState.selectedSetupType) {
                        LocalAuthType.PIN -> {
                            PinLockView(onPinCompleted = { viewModel.handleSecretInput(it) })
                        }

                        LocalAuthType.PASSWORD -> {
                            PasswordLockView(onPasswordSubmitted = { viewModel.handleSecretInput(it) })
                        }

                        else -> {
                            Box(Modifier.size(300.dp)) {
                                PatternLockView(onPatternCompleted = {
                                    viewModel.handleSecretInput(
                                        it
                                    )
                                })
                            }
                        }
                    }
                }

                else -> {
                    LaunchedEffect(Unit) {
                        onFinished()
                    }
                }
            }


            uiState.errorMessage?.let { errorMessage ->
                Spacer(Modifier.height(8.dp))

                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

        }
    }
}