package ir.ornix.passgen.feature.setup.impl.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.ornix.passgen.core.domain.MasterKeyRepository
import ir.ornix.passgen.feature.setup.impl.presentation.SetupViewModel

@Composable
fun SetupScreen(
    repository: MasterKeyRepository,
    onSetupComplete: () -> Unit
) {
    val viewModel: SetupViewModel = viewModel { SetupViewModel(repository) }

    SetupContent(
        masterKey = viewModel.masterKey,
        onKeyChange = viewModel::onKeyChange,
        onSaveClick = { viewModel.saveAndContinue(onSetupComplete) }
    )
}

@Composable
private fun SetupContent(
    masterKey: String,
    onKeyChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Setup Master Key", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = masterKey,
            onValueChange = onKeyChange,
            label = { Text("Enter Master Key") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save and Continue")
        }
    }
}
