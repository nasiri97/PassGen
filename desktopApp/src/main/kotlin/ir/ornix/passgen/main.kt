package ir.ornix.passgen

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ir.ornix.passgen.composeapp.App
import org.jetbrains.compose.resources.painterResource
import passgen.desktopapp.generated.resources.Res
import passgen.desktopapp.generated.resources.pass_gen

fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = "PassGen",
        icon = painterResource(Res.drawable.pass_gen)
    ) {
        App()
    }
}