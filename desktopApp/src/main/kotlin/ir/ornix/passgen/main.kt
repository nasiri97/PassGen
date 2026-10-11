package ir.ornix.passgen

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ir.ornix.passgen.composeapp.App
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.app_icon
import ir.ornix.passgen.core.ui.app_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.app_icon)
    ) {
        App()
    }
}