package ir.ornix.passgen.feature.random.impl.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.common.platform
import ir.ornix.passgen.core.domain.InstalledAppsProvider
import ir.ornix.passgen.core.model.Credential
import ir.ornix.passgen.core.model.PassGenItem
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.action_refresh_all
import ir.ornix.passgen.core.ui.add_config_content_desc
import ir.ornix.passgen.core.ui.add_first_config_button
import ir.ornix.passgen.core.ui.component.PullToRefreshWithHint
import ir.ornix.passgen.core.ui.copy_disabled_toast_message
import ir.ornix.passgen.core.ui.copy_success_toast_message
import ir.ornix.passgen.core.ui.no_config_message
import ir.ornix.passgen.core.ui.passgen.PassGenItemList
import ir.ornix.passgen.feature.random.impl.presentation.RandomIntent
import ir.ornix.passgen.feature.random.impl.presentation.RandomViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RandomScreen(
    onNavigateToCreateConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: RandomViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val copyDisabledMessage = stringResource(Res.string.copy_disabled_toast_message)
    val copySuccessMessage = stringResource(Res.string.copy_success_toast_message)

    val onCopyRequested: (Password) -> Unit = { password ->
        if (uiState.isCopyEnabled) {
            clipboardManager.setText(AnnotatedString(password.value.concatToString()))
            scope.launch {
                snackbarHostState.showSnackbar(copySuccessMessage)
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(copyDisabledMessage)
            }
        }
    }

    var isSnackbarAvailableInRandomList by remember { mutableStateOf(false) }

    val isSnackbarAvailableInChildren = isSnackbarAvailableInRandomList

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            if (!isSnackbarAvailableInChildren)
                SnackbarHost(snackbarHostState)
        },
        floatingActionButton = {
            if (uiState.passGenItems.isNotEmpty())
                FloatingActionButton(onClick = onNavigateToCreateConfig) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(Res.string.add_config_content_desc)
                    )
                }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        if (platform.isGestureRefreshSupported) {
            PullToRefreshWithHint(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.dispatch(RandomIntent.RefreshAllPasswords) },
                hasHintShown = uiState.hasRefreshHintShown || uiState.passGenItems.isEmpty(),
                onHintShown = { viewModel.dispatch(RandomIntent.RefreshHintShown) }
            ) {
                RandomList(
                    passGenItems = uiState.passGenItems,
                    onRemovePasswordItemClick = { viewModel.dispatch(RandomIntent.RemoveConfig(it.config)) },
                    addNewCredential = { viewModel.dispatch(RandomIntent.SaveCredential(it)) },
                    onNavigateToCreateConfig = onNavigateToCreateConfig,
                    onCopyRequested = onCopyRequested,
                    revealTimeoutMillis = uiState.visibilityTimeout.timeoutMillis,
                    snackbarHostState = snackbarHostState,
                    onSnackbarAvailabilityChanged = { isSnackbarAvailableInRandomList = it },
                    installedAppsProvider = viewModel.installedAppsProvider
                )
            }
        } else {
            RandomList(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                passGenItems = uiState.passGenItems,
                onRemovePasswordItemClick = { viewModel.dispatch(RandomIntent.RemoveConfig(it.config)) },
                addNewCredential = { viewModel.dispatch(RandomIntent.SaveCredential(it)) },
                onNavigateToCreateConfig = onNavigateToCreateConfig,
                onCopyRequested = onCopyRequested,
                revealTimeoutMillis = uiState.visibilityTimeout.timeoutMillis,
                snackbarHostState = snackbarHostState,
                onSnackbarAvailabilityChanged = { isSnackbarAvailableInRandomList = it },
                installedAppsProvider = viewModel.installedAppsProvider,
                onRefreshAll = { viewModel.dispatch(RandomIntent.RefreshAllPasswords) },
                isRefreshing = uiState.isRefreshing,
                showDesktopRefreshHeader = true
            )
        }
    }
}

@Composable
private fun RandomList(
    passGenItems: List<PassGenItem>,
    onRemovePasswordItemClick: (PassGenItem) -> Unit,
    addNewCredential: (credential: Credential) -> Unit,
    onNavigateToCreateConfig: () -> Unit,
    onCopyRequested: (Password) -> Unit,
    revealTimeoutMillis: Long,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
    onSnackbarAvailabilityChanged: (Boolean) -> Unit = {},
    installedAppsProvider: InstalledAppsProvider,
    onRefreshAll: () -> Unit = {},
    isRefreshing: Boolean = false,
    showDesktopRefreshHeader: Boolean = false
) {
    if (passGenItems.isEmpty()) {
        EmptyRandomContent(
            modifier = modifier,
            onAddClick = onNavigateToCreateConfig
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showDesktopRefreshHeader) {
                Spacer(Modifier.height(4.dp))
                DesktopRefreshHeader(
                    isRefreshing = isRefreshing,
                    onRefreshAll = onRefreshAll
                )
            }

            PassGenItemList(
                modifier = Modifier.weight(1f),
                passGenItems = passGenItems,
                removePasswordItem = onRemovePasswordItemClick,
                addNewCredential = addNewCredential,
                onCopyRequested = onCopyRequested,
                revealTimeoutMillis = revealTimeoutMillis,
                snackbarHostState = snackbarHostState,
                onSnackbarAvailabilityChanged = onSnackbarAvailabilityChanged,
                installedAppsProvider = installedAppsProvider
            )
        }
    }
}

@Composable
private fun DesktopRefreshHeader(
    isRefreshing: Boolean,
    onRefreshAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RefreshSpinTransition")
    val angle by if (isRefreshing) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "RefreshSpinAngle"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalButton(
            onClick = onRefreshAll,
            enabled = !isRefreshing,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(Res.string.action_refresh_all),
                modifier = Modifier
                    .size(18.dp)
                    .graphicsLayer { rotationZ = angle }
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.action_refresh_all),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmptyRandomContent(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.no_config_message),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAddClick) {
                Text(stringResource(Res.string.add_first_config_button))
            }
        }
    }
}
