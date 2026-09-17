package ir.ornix.passgen.feature.savedpasswords.impl.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.feature.savedpasswords.impl.presentation.SavedPasswordsViewModel

@Composable
fun SavedPasswordsScreen(modifier: Modifier = Modifier) {
    val viewModel: SavedPasswordsViewModel = viewModel { SavedPasswordsViewModel() }

    Box(
        modifier = modifier.secureContent().fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Saved Passwords Page", style = MaterialTheme.typography.headlineLarge)
    }
}
