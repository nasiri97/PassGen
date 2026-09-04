package ir.ornix.passgen.feature.settings.impl.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.ornix.passgen.feature.settings.impl.presentation.SettingsViewModel

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = viewModel { SettingsViewModel() }
    
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Settings Page", style = MaterialTheme.typography.headlineLarge)
    }
}
