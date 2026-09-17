package ir.ornix.passgen.feature.about.impl.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.ornix.passgen.core.ui.security.secureContent
import ir.ornix.passgen.feature.about.impl.presentation.AboutViewModel

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    val viewModel: AboutViewModel = viewModel { AboutViewModel() }

    Box(
        modifier = modifier.secureContent().fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("About Page", style = MaterialTheme.typography.headlineLarge)
    }
}
