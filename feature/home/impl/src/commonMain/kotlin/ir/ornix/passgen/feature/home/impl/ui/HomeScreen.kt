package ir.ornix.passgen.feature.home.impl.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.ornix.passgen.feature.home.impl.presentation.HomeViewModel

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = viewModel { HomeViewModel() }
    
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Home Page", style = MaterialTheme.typography.headlineLarge)
            Text("Welcome to PassGen")
        }
    }
}
