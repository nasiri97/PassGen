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
import ir.ornix.passgen.composeapp.FailedInitializationReason
import ir.ornix.passgen.composeapp.InitializationState
import ir.ornix.passgen.composeapp.di.initKoin
import ir.ornix.passgen.core.logging.Logger

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        AppStartup()
    }
}

@Composable
private fun AppStartup() {

    var initializationState by remember {
        mutableStateOf<InitializationState>(InitializationState.Initializing)
    }

    LaunchedEffect(Unit) {
        try {
            initKoin()
            initializationState = InitializationState.Completed
        } catch (e: Exception) {
            Logger.e("Koin initialization failed.", e)
            initializationState = InitializationState.Failed(
                FailedInitializationReason.INITIALIZATION_FAILED
            )
        }
    }

    App(
        initializationState = initializationState,
        onExit = {}
    )
}