package ir.ornix.passgen.feature.savedpasswords.impl.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.ornix.passgen.feature.savedpasswords.impl.presentation.SavedPasswordsViewModel

@Composable
fun SavedPasswordsScreen() {
    val viewModel: SavedPasswordsViewModel = viewModel { SavedPasswordsViewModel() }
    
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Saved Passwords Page", style = MaterialTheme.typography.headlineLarge)
    }
}
