package ir.ornix.passgen.feature.unlock.impl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.ui.lockview.PasswordLockView
import ir.ornix.passgen.core.ui.lockview.PatternLockView
import ir.ornix.passgen.core.ui.lockview.PinLockView
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.feature.unlock.impl.presentation.UnlockingGateIntent
import ir.ornix.passgen.feature.unlock.impl.presentation.UnlockingGateViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UnlockingGateScreen(
    modifier: Modifier = Modifier,
    onUnlocked: () -> Unit
) {

    val viewModel: UnlockingGateViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLocked) {
        if (!uiState.isLocked) {
            onUnlocked()
        }
    }

    LaunchedEffect(Unit) {
        if (uiState.shouldShowBiometricOption) {
            viewModel.dispatch(UnlockingGateIntent.BiometricClicked)
        }
    }


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
                text = "Application Locked",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(Modifier.height(24.dp))

            when (uiState.localAuthType) {
                LocalAuthType.PIN -> {
                    PinLockView(
                        onPinCompleted = { secret ->
                            viewModel.dispatch(UnlockingGateIntent.SecretSubmitted(secret))
                        },
                    )
                }

                LocalAuthType.PASSWORD -> {
                    PasswordLockView(
                        onPasswordSubmitted = { secret ->
                            viewModel.dispatch(UnlockingGateIntent.SecretSubmitted(secret))
                        },
                    )
                }

                LocalAuthType.PATTERN -> {
                    Box(
                        modifier = Modifier.size(300.dp),
                    ) {
                        PatternLockView(
                            onPatternCompleted = { secret ->
                                viewModel.dispatch(UnlockingGateIntent.SecretSubmitted(secret))
                            },
                        )
                    }
                }

                LocalAuthType.NONE -> Unit
            }

            if (uiState.shouldShowBiometricOption) {
                Spacer(Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .clickable(onClick = {
                            viewModel.dispatch(UnlockingGateIntent.BiometricClicked)
                        })
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Use fingerprint",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(36.dp),
                        )
                    }

                    Text(
                        text = "Use Fingerprint",
                    )
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