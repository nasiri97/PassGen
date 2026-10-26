package ir.ornix.passgen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import ir.ornix.passgen.composeapp.App
import ir.ornix.passgen.composeapp.DeviceSupportStatus
import ir.ornix.passgen.composeapp.di.initKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        AppStartup()
    }
}

@Composable
private fun AppStartup() {
    var isDbInitializationCompleted by remember { mutableStateOf(false) }
    var isDbInitializationFailed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            initKoin()
            isDbInitializationCompleted = true
        } catch (_: Exception) {
            isDbInitializationFailed = true
        }
    }

    val supportStatus = when {
        isDbInitializationFailed -> DeviceSupportStatus.INITIALIZATION_FAILED
        isDbInitializationCompleted -> DeviceSupportStatus.SUPPORTED
        else -> null
    }

    App(
        supportStatus = supportStatus,
        onExit = {}
    )
}