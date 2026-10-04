package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.add_config_content_desc
import ir.ornix.passgen.core.ui.add_first_config_button
import ir.ornix.passgen.core.ui.no_config_message
import ir.ornix.passgen.core.ui.passgen.PassGenItemList
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.feature.home.impl.presentation.HomeIntent
import ir.ornix.passgen.feature.home.impl.presentation.HomeViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToCreateConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val clipboardManager = LocalClipboardManager.current
    val onCopyRequested: (Password) -> Unit = {
        clipboardManager.setText(AnnotatedString(it.value.concatToString()))
    }

    Scaffold(
        modifier = modifier.secureContent().fillMaxSize(),
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
        if (uiState.passGenItems.isEmpty()) {
            EmptyHomeContent(
                modifier = Modifier.padding(innerPadding),
                onAddClick = onNavigateToCreateConfig
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                InputSection(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                    input = uiState.input,
                    onInputChange = {
                        viewModel.dispatch(HomeIntent.InputChanged(it))
                    },
                    onDone = { }
                )

                PassGenItemList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    passGenItems = uiState.passGenItems,
                    removePasswordItem = {
                        viewModel.dispatch(HomeIntent.RemoveConfig(it.config))
                    },
                    addNewAccount = { viewModel.dispatch(HomeIntent.SaveAccount(it)) },
                    onCopyRequested = onCopyRequested
                )
            }
        }
    }
}

@Composable
private fun EmptyHomeContent(
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
