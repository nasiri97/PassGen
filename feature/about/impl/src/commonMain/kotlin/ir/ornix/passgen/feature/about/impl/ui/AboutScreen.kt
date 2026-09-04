package ir.ornix.passgen.feature.about.impl.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.ornix.passgen.feature.about.impl.presentation.AboutViewModel

@Composable
fun AboutScreen() {
    val viewModel: AboutViewModel = viewModel { AboutViewModel() }
    
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("About Page", style = MaterialTheme.typography.headlineLarge)
    }
}
