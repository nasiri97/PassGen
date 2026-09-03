package ir.ornix.passgen.feature.setup.impl

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.russhwolf.multiplatform.settings.Settings

@Composable
fun SetupScreen(onSetupComplete: () -> Unit) {
    var masterKey by remember { mutableStateOf("") }
    val settings = remember { Settings() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Setup Master Key", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = masterKey,
            onValueChange = { masterKey = it },
            label = { Text("Enter Master Key") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (masterKey.isNotBlank()) {
                    settings.putString("master_key", masterKey)
                    onSetupComplete()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save and Continue")
        }
    }
}
