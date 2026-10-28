package ir.ornix.passgen

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ir.ornix.passgen.composeapp.App
import ir.ornix.passgen.composeapp.FailedInitializationReason
import ir.ornix.passgen.composeapp.InitializationState
import ir.ornix.passgen.composeapp.di.initKoin
import ir.ornix.passgen.core.logging.Logger
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.app_icon
import ir.ornix.passgen.core.ui.app_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {

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

    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.app_icon)
    ) {
        App(
            initializationState = initializationState,
            onExit = {}
        )
    }
}
