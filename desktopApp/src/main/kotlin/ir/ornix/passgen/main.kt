package ir.ornix.passgen

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ir.ornix.passgen.composeapp.App
import ir.ornix.passgen.composeapp.DeviceSupportStatus
import ir.ornix.passgen.composeapp.di.initKoin
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.app_icon
import ir.ornix.passgen.core.ui.app_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {

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

    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.app_icon)
    ) {
        App(
            supportStatus = supportStatus,
            onExit = {}
        )
    }
}