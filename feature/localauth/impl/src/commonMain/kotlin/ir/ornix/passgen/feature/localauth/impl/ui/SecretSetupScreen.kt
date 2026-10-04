package ir.ornix.passgen.feature.localauth.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_skip
import ir.ornix.passgen.core.ui.app_security_title
import ir.ornix.passgen.core.ui.confirm_password_label
import ir.ornix.passgen.core.ui.confirm_pattern_label
import ir.ornix.passgen.core.ui.confirm_pin_label
import ir.ornix.passgen.core.ui.enter_password_label
import ir.ornix.passgen.core.ui.enter_pattern_label
import ir.ornix.passgen.core.ui.enter_pin_label
import ir.ornix.passgen.core.ui.local_lock_setup_subtitle
import ir.ornix.passgen.core.ui.lockview.PasswordLockView
import ir.ornix.passgen.core.ui.lockview.PatternLockView
import ir.ornix.passgen.core.ui.lockview.PinLockView
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.core.ui.setup_password_button
import ir.ornix.passgen.core.ui.setup_pattern_button
import ir.ornix.passgen.core.ui.setup_pin_button
import ir.ornix.passgen.feature.localauth.impl.presentation.SecretSetupIntent
import ir.ornix.passgen.feature.localauth.impl.presentation.SecretSetupViewModel
import ir.ornix.passgen.feature.localauth.impl.presentation.SetupStage
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SecretSetupScreen(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
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
                text = stringResource(Res.string.app_security_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))

            when (uiState.setupStage) {
                SetupStage.CHOOSE_TYPE -> {
                    Text(
                        text = stringResource(Res.string.local_lock_setup_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = {
                            viewModel.dispatch(
                                SecretSetupIntent.LocalAuthTypeSelected(LocalAuthType.PIN)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(stringResource(Res.string.setup_pin_button))
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.dispatch(
                                SecretSetupIntent.LocalAuthTypeSelected(LocalAuthType.PASSWORD)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(stringResource(Res.string.setup_password_button))
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.dispatch(
                                SecretSetupIntent.LocalAuthTypeSelected(LocalAuthType.PATTERN)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(stringResource(Res.string.setup_pattern_button))
                    }
                    Spacer(Modifier.height(32.dp))
                    TextButton(onClick = {
                        viewModel.dispatch(SecretSetupIntent.SetupCancelled)
                    }) {
                        Text(stringResource(Res.string.action_skip))
                    }
                }

                SetupStage.ENTER_SECRET -> {
                    InsertPassword(
                        localAuthType = uiState.selectedSetupType,
                        isConfirming = false
                    ) {
                        viewModel.dispatch(SecretSetupIntent.SecretInserted(it))
                    }
                }

                SetupStage.CONFIRM_SECRET -> {
                    InsertPassword(
                        localAuthType = uiState.selectedSetupType,
                        isConfirming = true
                    ) {
                        viewModel.dispatch(SecretSetupIntent.SecretInserted(it))
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


@Composable
private fun InsertPassword(
    localAuthType: LocalAuthType,
    isConfirming: Boolean,
    modifier: Modifier = Modifier,
    onSecretSubmitter: (String) -> Unit
) {

    val label = if (isConfirming) {
        when (localAuthType) {
            LocalAuthType.PIN -> stringResource(Res.string.confirm_pin_label)
            LocalAuthType.PASSWORD -> stringResource(Res.string.confirm_password_label)
            else -> stringResource(Res.string.confirm_pattern_label)
        }
    } else {
        when (localAuthType) {
            LocalAuthType.PIN -> stringResource(Res.string.enter_pin_label)
            LocalAuthType.PASSWORD -> stringResource(Res.string.enter_password_label)
            else -> stringResource(Res.string.enter_pattern_label)
        }
    }

    Column(
        modifier = modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(24.dp))

        when (localAuthType) {
            LocalAuthType.PIN -> {
                PinLockView(onPinCompleted = { onSecretSubmitter(it) })
            }

            LocalAuthType.PASSWORD -> {
                PasswordLockView(onPasswordSubmitted = { onSecretSubmitter(it) })
            }

            LocalAuthType.PATTERN -> {
                Box(Modifier.size(300.dp)) {
                    PatternLockView(onPatternCompleted = { onSecretSubmitter(it) })
                }
            }

            else -> {}
        }
    }
}
